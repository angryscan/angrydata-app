🇷🇺 [Русский](README.ru.md)

# Angry Data Scanner
Welcome to the `Angry Data Scanner` repository. This repository provides a code, documentation and software for discovering sensitive data using simple algothims. 
The idea of this project is to provide fast and simple tool for people to find files with personal data, banking secrecy, card data, passwords/API-keys/secrects etc.

Big Data Scanner is a cross-platform tool for detecting valuable data like PII, banking secrecy, payments cards like Visa/Mastercard, passwords etc.
The scanner runs on a local machine and does not require administrator rights for installation.
It makes no changes to the files being checked.
Only read-access is required for scanning.
The project is open-source and supported by educational collectives.

## Supported file formats
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

## Supported data types
The scanner detects the following types of data:

| Data types                  | Internal types                                                             |
|-----------------------------|----------------------------------------------------------------------------|
| Personal data (text)        | `full name`, `address`, `e-mail`                                           |
| Personal data (numbers)     | `passport number`, `phone number`, `car licence`, `social security number` |
| Banking secrecy             | `account number`                                                           |
| Payment cards               | `card number`, `CVV/CVV2`                                                  |
| IP-адреса                   | `IPv4`, `IPv6`                                                             |
| Custom signatures           | `Possible to create custom signatures`                                     |
| TLS-certificates            | `TLS-certificates`                                                         |
| Passwords                   | `work in progress`                                                         |
| Source code                 | `Source code`                                                              |
| AI-models, embeded in files | `work in progress`                                                         |
| Synthetic data              | `work in progress`                                                         |

## Connectors
The scanner is intended to be a universal tool for scanning everything. Currently, the scanner can connect to the following resources:

| Connector                | Description                                              |
|--------------------------|----------------------------------------------------------|
| Network Folder/Directory | Scans files on remote directory like Windows environment |
| HDD/SDD                  | Scan local hard drive                                    |
| S3                       | Scan files  in S3                                        |
| HTTP/HTTPS               | Scans web site content                                   |
| Database                 | `work in progress`                                       |

## System Requirements
`Windows`, `Linux `
`400MB HDD` `4GB RAM` `1.3Ghz CPU`

## [Console mode](doc/CONSOLE.md)

## Releases

[![Latest release](https://img.shields.io/github/v/release/angryscan/angrydata-app?sort=semver)](https://github.com/angryscan/angrydata-app/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/angryscan/angrydata-app/total.svg)](https://github.com/angryscan/angrydata-app/releases)

- **Latest:** [https://github.com/angryscan/angrydata-app/releases/latest](https://github.com/angryscan/angrydata-app/releases/latest)
- **All releases:** [https://github.com/angryscan/angrydata-app/releases](https://github.com/angryscan/angrydata-app/releases)
