package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShortUrlHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final DynamoDbClient dynamoDb = DynamoDbClient.builder()
            .region(Region.AP_SOUTH_1)
            .build();
    private final String tableName = System.getenv("TABLE_NAME");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        try {
            Map<String, Object> requestContext = (Map<String, Object>) input.get("requestContext");
            Map<String, Object> http = (Map<String, Object>) requestContext.get("http");
            String httpMethod = (String) http.get("method");
            if ("POST".equalsIgnoreCase(httpMethod)) {
                String rawBody = (String) input.get("body");
                Map<String, Object> parsedBody = objectMapper.readValue(rawBody, Map.class);
                return handleShorten(input, parsedBody);
            } else if ("GET".equalsIgnoreCase(httpMethod)) {
                return handleRedirect(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("statusCode", 500, "body", "Internal server error: " + e.getMessage());
        }
        return Map.of("statusCode", 400, "body", "Unsupported method");
    }


    private Map<String, Object> handleShorten(Map<String, Object> input, Map<String, Object> bodyMap) {
        String longUrl = bodyMap.get("url").toString();
        String shortCode = UUID.randomUUID().toString().substring(0, 6);

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("shortCode", AttributeValue.fromS(shortCode));
        item.put("longUrl", AttributeValue.fromS(longUrl));

        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());

        return Map.of(
                "statusCode", 200,
                "body", "{\"shortUrl\": \"" + input.get("rawPath").toString().replace("/shorten", "/" + shortCode) + "\"}"
        );
    }


    private Map<String, Object> handleRedirect(Map<String, Object> input) {
        Map<String, String> pathParams = (Map<String, String>) input.get("pathParameters");
        String shortCode = pathParams.get("shortCode");

        Map<String, AttributeValue> key = Map.of("shortCode", AttributeValue.fromS(shortCode));

        GetItemResponse response = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build());

        if (!response.hasItem()) {
            return Map.of("statusCode", 404, "body", "Short URL not found");
        }

        String longUrl = response.item().get("longUrl").s();
        return Map.of(
                "statusCode", 302,
                "headers", Map.of("Location", longUrl)
        );
    }
}
