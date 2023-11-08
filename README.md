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

## How it works

So the flows are pretty simple with the main flows as follows: 


![alt text](/diagram.png)