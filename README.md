# Traceroute Parser
The project develops a program that takes tcpdump as input to parse the textual trace of traffic generated by Traceroute. Traceroute is a network diagnostic tool used to display the route and measure transit delays of packets across an IP network. The program analyzes ICMP messages within the tcpdump to determine each router's address along the path to the destination and calculates the time taken to send and receive data to each router.

## Design Overview
The project involves parsing a network trace dump generated by Traceroute to extract router addresses and the time taken to send and receive data to each router on the path. The program analyzes ICMP messages to identify the relevant information and generates output based on the extracted data.

## Functionality
`TracerouteParser`:
- Reads and parses the textual tcpdump trace generated by Traceroute, extracting relevant information such as ICMP messages and timestamps.
- Identifies and extracts the IP address of each router on the path from ICMP messages in the tcpdump trace.
- Computes the round-trip time (RTT) for sending and receiving data to each router based on the timestamps in the tcpdump trace.
- Detects ICMP time exceeded messages indicating the response of routers to packets with TTL=1, aiding in determining the IP address of the first router.
- Formats the extracted router information, including TTL, IP address, and computed RTT, and generates output in the specified format.
- Iterates through the tcpdump trace to process ICMP messages for all routers on the path, generating output for each router encountered.

## File Structure and Content
```
traceroute-parser/
├── compile.sh
├── README.md
├── resources/
│   ├── output.txt
│   └── sampletcpdump.txt
└── src/
    └── TracerouteParser.java
```