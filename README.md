🇷🇺 [Русский](README.ru.md)

[![Latest release](https://img.shields.io/github/v/release/angryscan/angrydata-app?sort=semver)](https://github.com/angryscan/angrydata-app/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/angryscan/angrydata-app/total.svg)](https://github.com/angryscan/angrydata-app/releases)
[![Release date](https://img.shields.io/github/release-date/angryscan/angrydata-app?label=release%20date&display_date=published_at&color=orange)](https://github.com/angryscan/angrydata-app/releases/latest)

# A tool with friendly UI to discover sensitive data in 2 clicks
**Angry Data Scanner** is a data security and privacy tool that uses pattern matching to automatically discover sensitive data stored in folders, web pages, S3, database.  
It helps organizations by identifying where sensitive data such as personally identifiable information (PII) and intellectual property is stored.   
The tool provides visibility where your sensitive data is stored.  

- Sensitive data (PII, payments cards etc) can be discover with 2 click
- No administrator rights required to run Angry Data Scanner  
- No additional software installation required  
- Works on Linux, Mac, and Windows

## Discovered sensitive data
The scanner detects the following types of data:

| Data types                  | Internal types                                                             |
|-----------------------------|----------------------------------------------------------------------------|
| Personal data (text)        | `full name`, `address`, `e-mail`                                           |
| Personal data (numbers)     | `passport number`, `phone number`, `car licence`, `social security number` |
| Banking secrecy             | `account number`                                                           |
| Payment cards               | `card number`, `CVV/CVV2`                                                  |
| IP-adresses                 | `IPv4`, `IPv6`                                                             |
| Custom signatures           | `Possible to create custom signatures`                                     |
| TLS-certificates            | `TLS-certificates`                                                         |
| Passwords                   | `work in progress`                                                         |
| Source code                 | `Source code`                                                              |
| AI-models, embeded in files | `work in progress`                                                         |
| Synthetic data              | `work in progress`                                                         |

## Supported file types
The scanner supports the following file formats:

| File Type                 | File Format                                          |
|---------------------------|------------------------------------------------------|
| MS Office (tables)        | `.xlsx` `.xls`                                       |
| MS Office (text)          | `.docx` `.doc`                                       |
| MS Office (presentation)  | `.pptx` `.potx` `.ppsx` `.pptm` `.ppt` `.pps` `.pot` |
| Open Office (tables)      | `.ods`                                               |
| Open Office (text)        | `.odt`                                               | 
| Open Office (presentation)| `.odp` `.otp`                                        |
| Adobe                     | `.pdf`                                               |
| Archives                  | `.zip` `.rar`                                        |
| Plain text                | `.txt` `.csv` `.xml` `.json` `.log`                  |

## Supported data sources
The scanner is intended to be a universal tool for scanning everything. Currently, the scanner can connect to the following resources:

| Connector                | Description                                              |
|--------------------------|----------------------------------------------------------|
| Network Folder/Directory | Scans files on remote directory like Windows environment |
| HDD/SDD                  | Scan local hard drive                                    |
| S3                       | Scan files  in S3                                        |
| HTTP/HTTPS               | Scans web site content                                   |
| Database                 | `work in progress`                                       |

## Use cases
We share some practical use cases how Angry Data Scanner is used in real world.

- Leak Hunting team need to scan a network folder and ensure that it does not contain a source code
- An employee scans the network file resource and deletes files containing card numbers to ensure compliance with PCI DSS requirements
- A banking employee scans network file resource to ensure that it does not contain personal data of VIP clients
- A boss scans a file resource of the sales team so they don’t have client contacts on a shared folder
- Law enforcements need to discover a traces of cryptocurrency on a laptop
- A cybersecurity officer need to validate that the database does not contain a personal data

## Key features
- **Ranking**: scanner puts high-value files (with most PII etc) first in the list
- View scanning history
- Download results of a scan in a SCV files
- You can right-clock on a folder and run a scanner to discovery sensitive dat in a floder
- Scanner can run via command line
- You can schedule a scan
- You can stop the scanning process if criteria is met
- You can move files with sensitive data into specified folder
- You can configure a number of CPU cores used for scanning


## System Requirements
`Windows`, `Linux `
`400MB HDD` `4GB RAM` `1.3Ghz CPU`

## [Console mode](doc/CONSOLE.md)

## Direct Download

| OS | Download                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|---|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Windows** | <a href="https://github.com/angryscan/angrydata-app/releases/latest/download/big-data-scanner.exe"><img src="https://img.shields.io/badge/Setup-x64-0078D6?style=for-the-badge&logo=windows" alt="Windows stable .exe"></a><br/> <a href="https://github.com/angryscan/angrydata-app/releases/latest/download/big-data-scanner-1.2.1-windows-amd64.zip"><img src="https://img.shields.io/badge/portable-x64-0078D6?style=for-the-badge&logo=windows" alt="Windows portable .zip"></a> |
| **Linux** | <a href="https://github.com/angryscan/angrydata-app/releases/latest/download/big-data-scanner_1.2.1_amd64.deb"><img src="https://img.shields.io/badge/DEB-X64-A81D33?style=for-the-badge&logo=debian" alt="Linux .deb (amd64)"></a><br/> <a href="https://github.com/angryscan/angrydata-app/releases/latest/download/big-data-scanner-1.2.1-linux-amd64.tar.gz"><img src="https://img.shields.io/badge/portable-x64-333?style=for-the-badge&logo=linux" alt="Linux portable binary"></a>                        |
| **macOS** | <img src="https://img.shields.io/badge/macOS-in%20progress-000000?style=for-the-badge&logo=apple" alt="macOS in progress">                                                                                                                                                                                                                                                                                                                                                |

