# Tokens in Next-Gen Corda

Unlike Corda 4, we don’t have an SDK for tokens in Next-Gen Corda;
the token’s functionality is brought into the core C5 platform.
We have also introduced a new Token Selection API, which enables a flow to claim
tokens exclusively and provides a way to merge and return fungible tokens satisfying a given amount.

In this sample, I will show you how you can create a tokenised car system,
a database to maintain information about a car and provide it to the necassary parties.

## Tokens app
It will be used to create and maintain information about a car while providing a copy to 
a third party (Like an insurance comapny) if needed.

In this app you can:
1. Write a flow to Create a Car Asset/State on Ledger. `IssueCarTokensFlow`
2. List out the Car entries you had. `ListCarFlow`
3. Claim and transfer the tokens to a new member. `TransferCarTokenFlow`

### Setting up

1. We will begin our test deployment with clicking the `startCorda`. This task will load up the combined Corda workers in docker.
   A successful deployment will allow you to open the REST APIs at: https://localhost:8888/api/v1/swagger#. You can test out some of the
   functions to check connectivity. (GET /cpi function call should return an empty list as for now.)
2. We will now deploy the cordapp with a click of `5-vNodeSetup` task. Upon successful deployment of the CPI, the GET /cpi function call should now return the meta data of the cpi you just upload

### Running the tokens app

In Corda 5, flows will be triggered via `POST /flow/{holdingidentityshorthash}` and flow result will need to be view at `GET /flow/{holdingidentityshorthash}/{clientrequestid}`
* holdingidentityshorthash: the id of the network participants, ie Bob, Faris, Charlie. You can view all the short hashes of the network member with another gradle task called `ListVNodes`
* clientrequestid: the id you specify in the flow requestBody when you trigger a flow.

#### Step 1: Create Car State
Pick a VNode identity, and get its short hash. (Let's pick Faris.).

Go to `POST /flow/{holdingidentityshorthash}`, enter the identity short hash(Faris's hash) and request body:
```
{
 "clientRequestId": "issue-1",
    "flowClassName": "com.r3.developers.samples.TokenisedCarRegistry.workflows.CarIssueFlow",
    "requestBody": {
"consumed": "False",
"mileage" : "0",
"makeAndModel" : "Toyota Supra 2024",
"lscTire": "20230604",
"lscOil" : "20230604",
"lscCoolant" : "20230604",
"lscFilter" : "20230604",
"lscBattery" : "20230604",
"amountOfTimesServiced" : "1",
"currentOwnerMileage" : "0",
"userName" : "Faris",
"thirdParty" : "CN=ADInsurance, OU=Test Dept, O=ADInsurance, L=AbuDhabi, C=AE",
"mods" : ["Exhaust Change", "Spoilers"],
"reasonsForRepair" : [],
"owner": "CN=Bob, OU=Test Dept, O=R3, L=London, C=GB",
"identifier" : "1",
"reasonForRequest" : "Issue a new vehicle"

        }
}
```

After trigger the IssueCarTokensFlow flow, hop to `GET /flow/{holdingidentityshorthash}/{clientrequestid}` and enter the short hash(Faris's hash) and clientrequestid to view the flow result

#### Step 2: List the car state
Go to `POST /flow/{holdingidentityshorthash}`, enter the identity short hash(Bob's hash) and request body:
```
{
    "clientRequestId": "list-1",
    "flowClassName": "com.r3.developers.samples.TokenisedCarRegistry.workflows.ListCarFlow",
    "requestBody": {}
}
```
After trigger the ListCarTokens flow, again, we need to hop to `GET /flow/{holdingidentityshorthash}/{clientrequestid}`
and check the result.

#### Step 3: Transfer the car token with `TransferCarTokenFlow`
In this step, Faris will request a service from the service centre.
Goto `POST /flow/{holdingidentityshorthash}`, enter the identity short hash and request body.
Use Bob's holdingidentityshorthash to fire this post API.
```
{
    "clientRequestId": "transfer-1",
    "flowClassName": "com.r3.developers.samples.TokenisedCarRegistry.workflows.TransferCarTokenFlow",
    "requestBody": {
"consumed": "False",
"mileage" : "11000",
"lscTire": "20231104",
"lscOil" : "20231104",
"lscCoolant" : "20231104",
"lscFilter" : "20231104",
"lscBattery" : "20231104",
"amountOfTimesServiced" : "2",
"currentOwnerMileage" : "11000",
"userName" : "Faris",
"thirdParty" : "CN=ADInsurance, OU=Test Dept, O=ADInsurance, L=AbuDhabi, C=AE",
"mods" : ["Exhaust Change", "Spoilers", "Speakers Changed"],
"reasonsForRepair" : ["Check Engine light"],
"owner": "CN=ADRepairCentre, OU=Test Dept, O=R3, L=AbuDhabi, C=AE",
"identifier" : "1",
"reasonForRequest" : "Service Check"
        }
}
```

After the service centre finishes servicing it and updates its information they need to send it back to Faris using the following:
```
{
    "clientRequestId": "transfer-2",
    "flowClassName": "com.r3.developers.samples.TokenisedCarRegistry.workflows.TransferCarTokenFlow",
    "requestBody": {
"consumed": "False",
"mileage" : "11000",
"lscTire": "20231104",
"lscOil" : "20231104",
"lscCoolant" : "20231104",
"lscFilter" : "20231104",
"lscBattery" : "20231104",
"amountOfTimesServiced" : "2",
"currentOwnerMileage" : "11000",
"userName" : "Faris",
"thirdParty" : "CN=ADInsurance, OU=Test Dept, O=ADInsurance, L=AbuDhabi, C=AE",
"mods" : ["Exhaust Change", "Spoilers", "Speakers Changed"],
"reasonsForRepair" : ["Check Engine light"],
"owner": "CN=Faris, OU=Test Dept, O=R3, L=AbuDhabi, C=AE",
"identifier" : "1",
"reasonForRequest" : "Return to owner"
        }
}
```

And as for the result of this flow, go to `GET /flow/{holdingidentityshorthash}/{clientrequestid}` and enter the required fields.

The same thing can be used to sell the car. By just changing the owner like so:

```
{
    "clientRequestId": "transfer-3",
    "flowClassName": "com.r3.developers.samples.TokenisedCarRegistry.workflows.TransferCarTokenFlow",
    "requestBody": {
"consumed": "False",
"mileage" : "11000",
"lscTire": "20231104",
"lscOil" : "20231104",
"lscCoolant" : "20231104",
"lscFilter" : "20231104",
"lscBattery" : "20231104",
"amountOfTimesServiced" : "2",
"currentOwnerMileage" : "11000",
"userName" : "Faris",
"thirdParty" : "CN=ADInsurance, OU=Test Dept, O=ADInsurance, L=AbuDhabi, C=AE",
"mods" : ["Exhaust Change", "Spoilers", "Speakers Changed"],
"reasonsForRepair" : ["Check Engine light"],
"owner": "CN=Bob, OU=Test Dept, O=R3, L=London, C=GB",
"identifier" : "1",
"reasonForRequest" : "Selling the car"
        }
}
```

#### Step 4: Confirm the car information updated
Go to `POST /flow/{holdingidentityshorthash}`, enter the identity short hash(Faris's hash) and request body:
```
{
    "clientRequestId": "list-2",
    "flowClassName": "com.r3.developers.samples.TokenisedCarRegistry.workflows.ListCarTokens",
    "requestBody": {}
}
```
Go to `POST /flow/{holdingidentityshorthash}`, enter the identity short hash(Charlie's hash) and request body:


Thus, we have concluded a full run through of the token app.
