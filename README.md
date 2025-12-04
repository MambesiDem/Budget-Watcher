Budget Watcher & SMS Generator – Mobile Financial Tracking System

This project showcases a complete mobile system designed to help users track their spending in real time using SMS notifications sent by financial institutions. The system consists of two Android apps:

1. SMS Generator App
A tool that simulates real banking SMS notifications by generating and sending random financial messages to an emulator. It supports custom SMS creation, date-range generation, and batch message sending. This allows realistic testing without accessing real banking data.

2. Budget Watcher App
An intelligent SMS-processing app that:
• Automatically reads all SMS messages on the device
• Filters out non-financial messages using dynamic, user-defined regular expressions
• Extracts transaction details such as type, date, amount, description, and account
• Tags transactions (e.g., food, transport, rent, entertainment)
• Allows users to add new filters and tags at runtime
• Aggregates spending by date range and tags
• Displays summaries with drill-down views to inspect each transaction

The system gives users an instant overview of how their money is being spent—without accessing bank accounts directly—making it a privacy-friendly, real-time budgeting assistant.
