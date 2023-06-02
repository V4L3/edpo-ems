# EMSai - EDPO Project Group 3

EMSai is an energy management systems provider.
The project was developed as part of the EDPO course at the University of St. Gallen.
For Part 1 the Sales, Inventory and Payment services have been implemented, enabeling a client to order a battery for an EMS and have it installed.

## Setup

To start the application, you need to have [Docker](https://www.docker.com/) installed.

run `docker-compose -f docker-compose.yml up` (Camunda Platform) or `docker-compose -f docker-compose-cloud.yml up` (Camunda Cloud) to start the Kafka, Zeebee, Kafdrop, Camunda Tasklist, Elasticsearch, Operate and Zookeeper Containers.

Then start your services individually.
To ensure correct behavior, make sure all the containers are running before starting the services.


## Architecture

### Architectural Decisions
ADR's can be found in the [doc/adr](doc/adr/) folder (specifically for Part 1 ADR 01 - 08).

### Overview
![main-flow.png](doc%2Fimages%2Fmain-flow.png)

## Services

### Sales
[Sales](sales/) handles the initial customer request and the sales process.

**Camunda flow**
![sales-process.png](doc%2Fimages%2Fsales-process.png)

### Inventory
[Inventory](inventory/) handles the inventory and the assembly of the order as well as the installation.

**Camunda flow**
![inventory-process.png](doc%2Fimages%2Finventory-process.png)

### Payment
[Payment](payment/) handles the payment process.

**Camunda flow**
![payment-process.png](doc%2Fimages%2Fpayment-process.png)

### Notification
[Notification](notification/) handles the communication with the customer.

### Frontend & API Gateway
[Frontend](frontend/) and [API](api/) handle the frontend of the application.
To run the frontend run `npm install` and `npm run dev` in the frontend folder.
The frontend is then available at localhost:3000.

## Scenarios

### Happy Path

#### Sales
1. Customer requests a battery via the frontend (localhost:3000/sales)
![offer-request-form.png](doc%2Fimages%2Foffer-request-form.png)
2. Sales service receives the request and starts the sales process
3. Battery size gets calculated
4. Human Task is created in the Tasklist (localhost:8181) to approve the battery size and create the offer
![create-offer-task.png](doc%2Fimages%2Fcreate-offer-task.png)
5. The offer is sent to the customer
   **Output**: Email gets logged in the console of the inventory service with the corresopnding link to the frontend (localhost:3000/sales?id={offerId})
6. Customer accepts the offer
![accept-offer-form.png](doc%2Fimages%2Faccept-offer-form.png)
7. Oder placed event is published

#### Inventory
1. Inventory service receives the order placed event
2. Inventory service starts the inventory process
3. Inventory service checks if the materials are available (hardcoded to false)
4. Components and complementary materials are ordered
![await-parts-operate.png](doc%2Fimages%2Fawait-parts-operate.png)
5. Arrival of the Components and complementary materials is confirmed via the frontend (localhost:3000/inventory?id={orderId}) The orderId needs to be manually copied from the operate and added as the url parameter.
![confirm-parts-form.png](doc%2Fimages%2Fconfirm-parts-form.png)
6. A Human task is created in the Tasklist to assemble the parts
![assemble-parts-task.png](doc%2Fimages%2Fassemble-parts-task.png)
7. A message is sent to the client to fix and appointment for the installation
   **Output**: Email gets logged in the console of the inventory service with the corresopnding link to the frontend (localhost:3000/inventory?id={orderId})
8. The appointment is confirmed via the frontend (localhost:3000/inventory?id={orderId})
![appointment-form.png](doc%2Fimages%2Fappointment-form.png)
9. A human task is created in the Tasklist to install the battery
![installation-task.png](doc%2Fimages%2Finstallation-task.png)
10. Installation confirmed event is published

#### Payment
1. Payment service receives the installation confirmed event
2. Payment service starts the payment process
3. The Invoice is sent to the customer
   **Output**: Email gets logged in the console of the payment service.
4. A reminder is sent to the customer
   **Output**: Email gets logged in the console of the payment service.
5. The payment is confirmed via the frontend (localhost:3000/payment) The invoiceId needs to be manually copied from the operate and added to the form to mark the invoice as paid.
![payment-operate.png](doc%2Fimages%2Fpayment-operate.png)
![confirm-invoice-form.png](doc%2Fimages%2Fconfirm-invoice-form.png)
6. The payment is confirmed via the ERP system

### Alternative Paths

#### Sales - battery calculation error
1. Customer requests a battery via the frontend (localhost:3000/sales
![request-offer-form_error.png](doc%2Fimages%2Frequest-offer-form_error.png)
2. Sales service receives the request and starts the sales process
3. Battery size gets calculated
4. An error occurs in the calculation (for demo purposes the error can be triggered by entering a negative number in the frontend)
   **Output**: Error message gets logged in the console of the sales service.
![calculation-error-operate.png](doc%2Fimages%2Fcalculation-error-operate.png)
5. A human task is created in the Tasklist to fix the error and manually calculate the battery size
6. Flow continues as in the happy path

#### Sales - offer rejected
1. Customer requests a battery via the frontend (localhost:3000/sales)
2. Sales service receives the request and starts the sales process
3. Battery size gets calculated
4. Human Task is created in the Tasklist (localhost:8181) to approve the battery size and create the offer
5. The offer is sent to the customer
   **Output**: Email gets logged in the console of the notification service with the corresponding link to the frontend (localhost:3000/sales?id={offerId})
6. Customer rejects the offer
7. Offer is marked as cancelled and the process is terminated

#### Sales - client requests a new offer
1. Customer requests a battery via the frontend (localhost:3000/sales)
2. Sales service receives the request and starts the sales process
3. Battery size gets calculated
4. Human Task is created in the Tasklist (localhost:8181) to approve the battery size and create the offer
5. The offer is sent to the customer
   **Output**: Email gets logged in the console of the notification service with the corresponding link to the frontend (localhost:3000/sales?id={offerId})
6. Customer requests a new offer
7. A human task is requested in the Tasklist to create a new offer
8. The new offer is sent to the customer
   **Output**: Email gets logged in the console of the notification service with the corresponding link to the frontend (localhost:3000/sales?id={offerId})
9. Customer accepts the offer
10. Flow continues as in the happy path

#### Sales - client does not respond
1. Customer requests a battery via the frontend (localhost:3000/sales)
2. Sales service receives the request and starts the sales process
3. Battery size gets calculated
4. Human Task is created in the Tasklist (localhost:8181) to approve the battery size and create the offer
5. The offer is sent to the customer
   **Output**: Email gets logged in the console of the notification service with the corresponding link to the frontend (localhost:3000/sales?id={offerId})
6. Customer does not respond
7. A reminder is sent to the customer
   **Output**: Email gets logged in the console of the notification service.
8. Customer does not respond
9. Offer is marked as cancelled and the process is terminated

#### Payment - invoice not paid 
1. Payment service receives the installation confirmed event
2. Payment service starts the payment process
3. The Invoice is sent to the customer
   **Output**: Email gets logged in the console of the notification service.
4. A reminder is sent to the customer
   **Output**: Email gets logged in the console of the notification service.
5. Customer does not respond
6. After 4 reminders the invoice is escalated to the legal department and the process is terminated.
   **Output**: Command gets logged in the console of the payment service.