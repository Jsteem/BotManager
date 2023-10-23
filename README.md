# Bot Manager

At the heart of this project is the open-source botting client, [OSRSBot](https://github.com/OSRSB/OsrsBot). OSRSBot is an API designed to aid in automating the gameplay of Old School RuneScape, a popular MMORPG.



## Setup

The Bot Manager, built with Spring Boot, acts as the central control hub for overseeing bot activities. External botting clients seamlessly interact with the system by making REST API calls, facilitating communication across multiple servers.


Upon launching the botting client in [Application.java](./OSRSBot/src/main/java/net/runelite/rsb/botLauncher/Application.java), the client initiates communication with the manager. This exchange provides the client with essential login details, including proxy information, credentials, and the startup script.



## Scripting


This client's advantage lies in its scripting capabilities. By harnessing the power of the Java Reflection API, dynamic loading of classes at runtime becomes possible. The script project rebuilds the script files and the botting client loads these new class files without the need of rebuilding the client itself.  

Within the [script folder](./script-template/src/main/java/), a diverse array of scripts can be discovered. These scripts serve various purposes, such as leveling up accounts, completing quests, and generating in-game currency. The Bot Manager is responsible for scheduling which script runs on each specific account.


## Manager


The [AccountController](./botmanager/src/main/java/botmanager/api/AccountController.java) represents an API endpoint controller class. This class handles various HTTP requests related to account management. It exposes endpoints for updating account names, skills, retrieving account information and managing specific account values.


The [AccountService](./botmanager/src/main/java/botmanager/application/AccountService.java) handles various account-related operations. It has logic to choose what accounts are best suited to login and what script to run.


The [Account](./botmanager/src/main/java/botmanager/domain/Account.java) class contains the data of each account. It is used by the JpaRepository to represent a table in the database.



