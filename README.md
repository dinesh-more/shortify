# 🔗 Shortify - Serverless URL Shortener & Redirect to Long URL with AWS

**Shortify** is a lightweight, blazing-fast URL shortening service powered entirely by AWS serverless architecture. It allows users to submit a long URL and receive a shortened, easy-to-share link — all without managing servers!

> Built with ❤️ using **Java 21**, **AWS Lambda**, **API Gateway**, **DynamoDB**, and **Terraform**.

---

## ✨ Features

- 🔧 Fully serverless and scalable with AWS Lambda & API Gateway
- ⚡ Fast and cost-effective with pay-per-use infrastructure
- 🔐 Secure: Uses IAM roles for fine-grained permissions
- 🧩 Easily extensible for analytics, expiration, or custom slugs
- ☁️ Infrastructure as Code with Terraform

---

## 📐 Architecture Overview

```text
+-----------+         +------------------+         +-----------------+
|  Client   |  <--->  | API Gateway REST |  <--->  |  Lambda Handler |
+-----------+         +------------------+         +-----------------+
                                                       |
                                                       V
                                              +-------------------+
                                              |   DynamoDB Table  |
                                              | shortify_details  |
                                              +-------------------+
```

---

## 🚀 Getting Started

### 1. Clone the Repo

```bash
git clone https://github.com/your-username/shortify.git
cd shortify
```

### 2. Setup AWS Credentials

Make sure your AWS credentials are configured (via `~/.aws/credentials` or environment variables).

### 3. Deploy Infrastructure

Using Terraform:

```bash
cd shortify
terraform init
terraform plan -auto-approve
terraform apply -auto-approve
```

This will provision:
- IAM Role for Lambda
- API Gateway (HTTP API)
- Lambda function (Java)
- DynamoDB table

### 4. Package & Deploy the Lambda (if not using Terraform for code)

Use Maven to package:

```bash
./mvn clean package
```

Upload the JAR to AWS Lambda or through Terraform.

---

## 📬 API Usage

### `POST /shorten`

Create a shortened URL.

**Request:**

```http
POST /shorten
Content-Type: application/json

{
  "url": "https://your-long-url.com"
}
```

**Response:**

```json
{
  "shortUrl": "https://abc123"
}
```

### `GET /{shortId}`

Redirects to the original long URL.

Example:

```
GET /abc123 -> 302 Redirect to https://your-long-url.com
```

---

## 🧪 Testing Locally

You can use tools like Postman or `curl`:

```bash
curl -X POST https://<api-id>.execute-api.ap-south-1.amazonaws.com/shorten   -H "Content-Type: application/json"   -d '{"url": "https://example.com"}'
```

---

## 📁 Project Structure

```
.
├── main.tf         # Terraform code
├── src/main/java/
│   └── org.example/
│       └── ShortUrlHandler.java  # Lambda logic
├── pom.xml  # Java project build config
├── README.md
└── postman.json
```

---

## 🛡️ Security Notes

- IAM roles follow the principle of least privilege.
- All input is validated in the Lambda handler.
- You can extend the logic to add expiration, access limits, etc.

---

## 🧠 Future Enhancements

- 📊 Track click analytics
- ⏳ Add expiration support
- 🔑 Support custom short URLs
- 🌍 Custom domain support with Route 53
- 🔐 API authentication with JWT

---

## 🧰 Built With

- [Java 21](https://www.oracle.com/java/)
- [AWS Lambda](https://aws.amazon.com/lambda/)
- [Amazon API Gateway](https://aws.amazon.com/api-gateway/)
- [Amazon DynamoDB](https://aws.amazon.com/dynamodb/)
- [Terraform](https://www.terraform.io/)

---

## 🤝 Contributing

Pull requests are welcome! If you'd like to contribute features or improvements, feel free to open an issue or PR.

---

## 📜 License

MIT License. See [LICENSE](LICENSE) for details.

---

## 🙌 Acknowledgements

Thanks to the AWS community for extensive documentation and tooling support!
