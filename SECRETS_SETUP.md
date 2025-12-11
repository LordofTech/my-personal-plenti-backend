# Secrets Setup Guide

This guide explains how to set up secrets for the Plenti Backend application in different environments.

## Table of Contents
1. [Development (Local)](#development-local)
2. [Docker Compose](#docker-compose)
3. [Kubernetes](#kubernetes)
4. [Production Best Practices](#production-best-practices)

## Development (Local)

### Step 1: Create .env file

Copy the `.env.example` file to `.env`:

```bash
cp .env.example .env
```

### Step 2: Fill in the values

Edit the `.env` file and replace placeholder values with your actual credentials:

```bash
# Database - Use local MySQL or Docker
DB_URL=jdbc:mysql://localhost:3306/plenti_db?createDatabaseIfNotExist=true&autoReconnect=TRUE
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT Secret - Generate a secure random string (minimum 256 bits)
JWT_SECRET=$(openssl rand -base64 64)

# Huawei OBS - Get from Huawei Cloud Console
HUAWEI_OBS_ACCESS_KEY_ID=your_access_key
HUAWEI_OBS_SECRET_ACCESS_KEY=your_secret_key
HUAWEI_OBS_BUCKET_NAME=plenti-storage

# Termii - Get from https://termii.com
TERMII_API_KEY=your_termii_key

# Monnify - Get from https://monnify.com
MONNIFY_API_KEY=your_api_key
MONNIFY_SECRET_KEY=your_secret_key
MONNIFY_CONTRACT_CODE=your_code

# Email - Use Gmail App Password
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

### Step 3: Generate JWT Secret

Generate a secure JWT secret:

```bash
openssl rand -base64 64
```

Copy the output and set it as `JWT_SECRET` in your `.env` file.

## Docker Compose

For Docker Compose, environment variables can be set in the `docker-compose.yml` file or in a separate `.env` file.

### Option 1: Using .env file (Recommended)

Docker Compose automatically loads `.env` file from the project root. Just create your `.env` file as described above.

### Option 2: Direct in docker-compose.yml

Edit `docker-compose.yml` and update the environment section:

```yaml
services:
  plenti-backend:
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/plenti_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      # ... other variables
```

## Kubernetes

### Step 1: Create the Secret

1. Copy `k8s-secrets.yaml` to `k8s-secrets.local.yaml` (this file is gitignored):

```bash
cp k8s-secrets.yaml k8s-secrets.local.yaml
```

2. Edit `k8s-secrets.local.yaml` and fill in your actual values.

3. Apply the secret to your cluster:

```bash
kubectl apply -f k8s-secrets.local.yaml
```

### Step 2: Update Deployment

Uncomment the secret reference in `deployment.yaml`:

```yaml
envFrom:
  - secretRef:
      name: plenti-backend-secret
```

And remove or comment out the hardcoded environment variables.

### Step 3: Apply Deployment

```bash
kubectl apply -f deployment.yaml
```

### Alternative: Using kubectl create secret

Instead of a YAML file, you can create secrets directly:

```bash
kubectl create secret generic plenti-backend-secret \
  --namespace=staging-motopay-ns \
  --from-literal=DB_URL='jdbc:mysql://...' \
  --from-literal=DB_USERNAME='root' \
  --from-literal=DB_PASSWORD='your_password' \
  --from-literal=JWT_SECRET='your_jwt_secret' \
  --from-literal=HUAWEI_OBS_ACCESS_KEY_ID='your_key' \
  --from-literal=HUAWEI_OBS_SECRET_ACCESS_KEY='your_secret' \
  # ... add all other secrets
```

### Verify Secret Creation

```bash
kubectl get secrets -n staging-motopay-ns
kubectl describe secret plenti-backend-secret -n staging-motopay-ns
```

## Production Best Practices

### 1. Never Commit Secrets to Git

- Always use `.gitignore` for `.env` and `k8s-secrets.local.yaml`
- Never commit actual secrets in code or configuration files
- Use placeholder values in example files

### 2. Use Secure Secret Management

For production, consider using:

- **Kubernetes Secrets with encryption at rest**
- **HashiCorp Vault** - Industry standard for secret management
- **AWS Secrets Manager** / **Azure Key Vault** / **GCP Secret Manager**
- **Sealed Secrets** - For GitOps workflows

### 3. Rotate Secrets Regularly

- Rotate database passwords every 90 days
- Rotate API keys every 180 days
- Rotate JWT secrets if compromised

### 4. Principle of Least Privilege

- Use separate credentials for dev, staging, and production
- Grant only necessary permissions to service accounts
- Use read-only database users where applicable

### 5. Audit and Monitor

- Enable audit logging for secret access
- Monitor for unusual API key usage
- Set up alerts for failed authentication attempts

## Obtaining API Keys

### Huawei OBS

1. Log in to [Huawei Cloud Console](https://console.huaweicloud.com)
2. Navigate to **My Credentials** > **Access Keys**
3. Create a new access key
4. Create an OBS bucket in your desired region
5. Note the bucket name and endpoint URL

### Termii SMS

1. Sign up at [Termii](https://termii.com)
2. Navigate to **API** section in dashboard
3. Copy your API Key
4. Configure sender ID (requires approval)

### Monnify Payment

1. Sign up at [Monnify](https://monnify.com)
2. Complete KYC verification
3. Navigate to **Settings** > **API Keys**
4. Copy API Key, Secret Key, and Contract Code
5. Start with sandbox environment for testing

### Gmail App Password

1. Enable 2-Factor Authentication on your Google account
2. Go to [App Passwords](https://myaccount.google.com/apppasswords)
3. Select **Mail** and your device
4. Copy the generated 16-character password
5. Use this as `MAIL_PASSWORD` (not your regular Gmail password)

## Testing Configuration

After setting up secrets, test the configuration:

```bash
# Test database connection
./mvnw spring-boot:run

# Check logs for successful connections
tail -f logs/application.log

# Test specific services
curl http://localhost:8080/actuator/health
```

## Troubleshooting

### Issue: JWT Secret too short

**Error:** `The specified key byte array is X bits which is not secure enough`

**Solution:** Generate a longer secret (minimum 256 bits):
```bash
openssl rand -base64 64
```

### Issue: Database connection refused

**Error:** `Communications link failure`

**Solution:** 
- Verify database is running: `mysql -u root -p`
- Check DB_URL, DB_USERNAME, and DB_PASSWORD
- Ensure MySQL is accessible on the specified port

### Issue: OBS upload fails

**Error:** `Access denied` or `Invalid credentials`

**Solution:**
- Verify access key and secret key
- Check bucket name and region
- Ensure bucket has proper permissions

### Issue: SMS not sending

**Error:** `Invalid API key` or `Sender ID not approved`

**Solution:**
- Verify TERMII_API_KEY
- Check if sender ID is approved
- Ensure sufficient SMS credits

## Support

For additional help:
- Check application logs: `logs/application.log`
- Review Spring Boot documentation
- Contact DevOps team for production issues
