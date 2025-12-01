

### `README.md`

````markdown
# Plenti Backend

Plenti is a digital marketplace simplifying FMCG restocking with 60-minute delivery or free, zero stockouts, and transparent pricing. It uses a hybrid fulfillment model with dark stores and verified partner stores.

## Features:
- **Faster than Konga** (2-6 hrs), more reliable than Jumia (stockouts), and inclusive vs. Temu (delays).
- **Target Audience**: Households, professionals, small businesses.
- **Core Features**:
  - Discovery with live search, product recommendations, and flash promotions.
  - Cart and wishlist functionality.
  - Guest and logged-in checkout flows.
  - Payment integration (Monnify).
  - Real-time order tracking and fulfillment.
  - Admin dashboard for user and order management.

## Prerequisites

Before running the app, make sure the following are set up:

1. **Docker**: Ensure Docker is installed and running on your machine.
   - [Install Docker](https://docs.docker.com/get-docker/)
2. **MySQL**: You should have MySQL installed and a database set up.
   - [Install MySQL](https://dev.mysql.com/doc/refman/8.0/en/installing.html)
3. **Elasticsearch**: You should have Elasticsearch running, as it is used for product search and indexing.
   - [Install Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/install-elasticsearch.html)

### Docker Setup (Recommended)

If you don’t want to manually install Elasticsearch or MySQL, you can use Docker to run these services.

```bash
docker-compose up -d
````

This will bring up the required services (Elasticsearch, MySQL) using Docker Compose.

## Setting up the Backend

### Step 1: Clone the Repository

Clone the repository to your local machine.

```bash
git clone https://github.com/your-repo/plenti-backend.git
cd plenti-backend
```

### Step 2: Build the Application

Make sure you have **Maven** installed on your machine to build the application. If you don’t have it, [install Maven](https://maven.apache.org/install.html).

Run the following command to install dependencies and build the application:

```bash
mvn clean install
```

### Step 3: Set Up the Database

* **MySQL**: Ensure MySQL is running and that the `plenti_db` database exists.

  * You can use the following SQL commands to create the database and user:

    ```sql
    CREATE DATABASE plenti_db;
    CREATE USER 'plenti_user'@'localhost' IDENTIFIED BY 'password';
    GRANT ALL PRIVILEGES ON plenti_db.* TO 'plenti_user'@'localhost';
    FLUSH PRIVILEGES;
    ```

* **Elasticsearch**: Make sure Elasticsearch is running at `http://localhost:9200`.

* Update your `src/main/resources/application.properties` file to match the MySQL and Elasticsearch configurations:

  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/plenti_db?useSSL=false
  spring.datasource.username=plenti_user
  spring.datasource.password=password
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true

  spring.elasticsearch.uris=http://localhost:9200
  ```

### Step 4: Run the Application

Once everything is set up, run the application using Maven:

```bash
mvn spring-boot:run
```

The application will start on port `8080` by default.

### Step 5: Accessing the API

Once the backend is running, you can access the API documentation through Swagger.

* **Swagger UI**: Navigate to the following URL to view the interactive API documentation:

  ```
  http://localhost:8080/swagger-ui/index.html#/
  ```

### Step 6: Sample Endpoints

You can test the following endpoints:

1. **POST /api/auth/signup** - Sign up new users.
2. **GET /api/products** - Get all products.
3. **POST /api/orders** - Place a new order.
4. **GET /api/orders/{orderId}/track** - Track an order.
5. **POST /api/payments/initiate** - Initiate a payment using Monnify.

### Step 7: Testing the Application

To test the backend:

* Use **Postman** or any HTTP client to send requests to the API.
* Use the **Swagger UI** to explore available endpoints.

### Environment Variables

If you're deploying the application, make sure the following environment variables are set:

* **SPRING_PROFILES_ACTIVE**: Set this to `prod` for production.
* **MYSQL_HOST**: The MySQL database hostname, usually `mysql` in Docker.
* **MYSQL_PORT**: Default MySQL port, `3306`.
* **ELASTICSEARCH_HOST**: The Elasticsearch service hostname, usually `localhost` in local setups.

### Step 8: Dockerizing the Application (Optional)

If you want to containerize the application, use the following **Dockerfile** to build the image.

```Dockerfile
# Use an official Maven image to build the application
FROM maven:3.8.1-openjdk-17 AS build

# Set the working directory to /app
WORKDIR /app

# Copy the pom.xml and the src directory to the working directory
COPY pom.xml .
COPY src ./src

# Run Maven to build the project and package the application into a JAR file
RUN mvn clean package -DskipTests

# Use an official OpenJDK runtime image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/plenti-backend-0.0.1-SNAPSHOT.jar plenti-backend.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "plenti-backend.jar"]
```

### Step 9: Docker Compose

 `docker-compose.yml` file to run the backend along with MySQL and Elasticsearch:

```yaml
version: '3.8'
services:
  plenti-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/plenti_db
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=rootpassword
      - SPRING_ELASTICSEARCH_URIS=http://elasticsearch:9200
    depends_on:
      - mysql
      - elasticsearch

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: plenti_db
    ports:
      - "3306:3306"
  
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.10.0
    environment:
      - discovery.type=single-node
    ports:
      - "9200:9200"
```

### Conclusion

By following the steps in this README, you should be able to set up and run the Plenti backend application locally, access the Swagger UI for API documentation, and test various endpoints. Make sure to check all configurations and ensure that MySQL and Elasticsearch are properly set up to ensure smooth functionality.



