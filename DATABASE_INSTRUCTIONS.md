## Setting up Database and Flyway Migration for PostgreSQL

### Prerequisites

- Ensure you have PostgreSQL installed and running on your machine.
- Ensure you have Maven installed.

### Step 1: Configure PostgreSQL Database

1. Create a new PostgreSQL database:
   ```sh
   psql -U postgres
   CREATE DATABASE tms;
   CREATE USER tmsservice WITH ENCRYPTED PASSWORD '123456';
   GRANT ALL PRIVILEGES ON DATABASE tms TO tmsservice;
   ```

### Step 2: Configure Flyway Migration

1. Add Flyway dependency in your `pom.xml` file:

   ```xml
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
       <version>9.22.3</version>
   </dependency>
   ```

2. Configure Flyway plugin in your `pom.xml` file:
   ```xml
   <plugin>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-maven-plugin</artifactId>
       <version>9.22.3</version>
       <configuration>
           <url>jdbc:postgresql://localhost:5432/tms</url>
           <user>tmsservice</user>
           <password>123456</password>
       </configuration>
   </plugin>
   ```

### Step 3: Create Migration Scripts

1. Create a directory for migration scripts:

   ```sh
   mkdir -p src/main/resources/db/migration
   ```

2. Add your migration scripts in the `src/main/resources/db/migration` directory. For example, create a file named `V1__Create_Employee_Table.sql` with the following content:
   ```sql
   CREATE TABLE employees (
       id VARCHAR(255) PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       email VARCHAR(255) NOT NULL,
       contact_number VARCHAR(255),
       role VARCHAR(255),
       created_at BIGINT
   );
   ```

### Step 4: Configure Flyway to Run Migrations Automatically

1. Add the following properties to your `application.properties` file to configure Flyway to run migrations automatically whenever you run the Spring Boot application:

   ```properties
   spring.flyway.url=jdbc:postgresql://localhost:5432/tms
   spring.flyway.user=tmsservice
   spring.flyway.password=123456
   spring.flyway.locations=classpath:db/migration
   spring.flyway.enabled=true
   ```

### Step 5: Run Migrations Manually

If you prefer to run the migrations manually instead of automatically, you can use the Flyway Maven plugin. Follow these steps:

1. Open a terminal and navigate to the root directory of your project.

2. Run the following command to execute the migrations:

   ```sh
   mvn flyway:migrate
   ```

This command will connect to the database using the configuration specified in the `pom.xml` file and apply any pending migrations.

Note: Ensure that the database is running and accessible before executing the command.

### Step 6: Handle Database Changes

When there are changes to the database schema, follow these steps to ensure the changes are properly managed and applied:

1. Create a new migration script in the `src/main/resources/db/migration` directory. Name the file with an incremented version number and a descriptive name. For example, if the last migration script was `V1__Create_Employee_Table.sql`, name the new script `V2__Add_Department_Column.sql`.

2. Add the necessary SQL statements to the new migration script to apply the changes. For example, to add a new column to the `employees` table, the script might look like this:

   ```sql
   ALTER TABLE employees ADD COLUMN department VARCHAR(255);
   ```

3. Save the migration script and commit it to your version control system.

4. If you are running migrations automatically, restart your Spring Boot application to apply the new migration. If you are running migrations manually, execute the following command to apply the new migration:

   ```sh
   mvn flyway:migrate
   ```

5. Verify that the changes have been applied successfully by checking the database schema and running any necessary tests.

By following these steps, you can ensure that any changes to the database schema are properly managed and applied in a consistent manner.
