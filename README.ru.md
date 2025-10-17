---
title: "Быстрый сканер данных с удобным интерфейсом|angrydata-app"
layout: default
---
🇺🇸 [English](index.md)

[![Latest release](https://img.shields.io/github/v/release/angryscan/angrydata-app?sort=semver)](https://github.com/angryscan/angrydata-app/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/angryscan/angrydata-app/total.svg)](https://github.com/angryscan/angrydata-app/releases)

Big Data Scanner - кроссплатформенный сканер, выявляющих ценные данные (далее сканер). Сканер запускается на локальной машине и не требует для установки прав администратора.
Сканер не вносит никаких изменений в проверяемые файлы. Для сканирования требуется доступ только для чтения.
Проект - open-source и поддерживается образовательными коллективами.

## Форматы файлов
Сканер поддерживает следующие форматы файлов:

| Типа файлов               | Форматы файлов                                       |
|---------------------------|------------------------------------------------------|
| MS Office (таблицы)       | `.xlsx` `.xls`                                       |
| MS Office (текст)         | `.docx` `.doc`                                       |
| MS Office (презентации)   | `.pptx` `.potx` `.ppsx` `.pptm` `.ppt` `.pps` `.pot` |
| Open Office (таблицы)     | `.ods`                                               |
| Open Office (текст)       | `.odt`                                               | 
| Open Office (презентации) | `.odp` `.otp`                                        |
| Adobe                     | `.pdf`                                               |
| Архивы                    | `.zip` `.rar`                                        |
| Tекстовые                 | `.txt` `.csv` `.xml` `.json` `.log`                  |

## Типы данных
Сканер выявляет следующие типы данных:

| Типы данных, которые выявляет сканер | Микроданные, внутри типа данных                  |
|--------------------------------------|--------------------------------------------------|
| Персональные данные (текст)          | `ФИО`, `адрес`, `e-mail`                         |
| Персональные данные (цифры)          | `паспорт`, `телефон`, `машина`, `СНИЛС`, `ОМС`   |
| Банковская тайна                     | `номер счета`                                    |
| Данные платежных карт                | `номер карты`, `CVV/CVV2`                        |
| IP-адреса                            | `IPv4`, `IPv6`                                   |
| Собственные сигнатуры                | `Возможно написать собственные сигнатуры поиска` |
| TLS-сертификаты                      | `Детектор залежелей TLS-сертификатов в папках`   |
| Пароли                               | `в разработке`                                   |
| Исходных код                         | `Поиск файлов с исходным кодом`                  |
| AI-модели, cпрятанные в файлах       | `в разработке`                                   |
| Синтетические данные                 | `в разработке`                                   |

## Коннекторы
Предполагается, что сканер является универсальным средством для сканирования всего. В настоящее время сканер может подключатся к следующим ресурсам:

| Коннектор         | Описание                           |
|-------------------|------------------------------------|
| Файловые каталоги | Сканирует сетевые папки CIFS / NFS |
| HDD/SDD           | Сканирует локальные жесткие диски  |
| S3                | Сканирует дерево каталогов S3      |
| HTTP/HTTPS        | Сканирует веб-страницы             |
| СУБД              | `в разработке`                     |

## Системные требования
Поддержка ОС: 
`Windows`, `Linux (Astra, Ubuntu)`

Системные требования:
`400MB HDD` `4GB RAM` `1.3Ghz CPU`

## [Консольный режим](doc/CONSOLE.ru.md)

## Прямая загрузка

| Операционная система | Скачать                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Windows**          | <a href="https://github.com/angryscan/angrydata-app/releases/latest/download/big-data-scanner.exe"><img src="https://img.shields.io/badge/Setup-x64-0078D6?style=for-the-badge&logo=windows" alt="Windows stable .exe"></a><br/> <a href="https://github.com/angryscan/angrydata-app/releases/latest/download/big-data-scanner-1.2.1-windows-amd64.zip"><img src="https://img.shields.io/badge/portable-x64-0078D6?style=for-the-badge&logo=windows" alt="Windows portable .zip"></a>     |
| **Linux**            | <a href="https://github.com/angryscan/angrydata-app/releases/latest/download/big-data-scanner_1.2.1_amd64.deb"><img src="https://img.shields.io/badge/DEB-X64-A81D33?style=for-the-badge&logo=debian" alt="Linux .deb (amd64)"></a><br/> <a href="https://github.com/angryscan/angrydata-app/releases/latest/download/big-data-scanner-1.2.1-linux-amd64.tar.gz"><img src="https://img.shields.io/badge/portable-x64-333?style=for-the-badge&logo=linux" alt="Linux portable binary"></a> |
| **macOS**            | <img src="https://img.shields.io/badge/macOS-in%20progress-000000?style=for-the-badge&logo=apple" alt="macOS in progress">                                                                                                                                                                                                                                                                                                                                                                |
