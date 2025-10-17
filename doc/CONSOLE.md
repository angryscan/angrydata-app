## Console Mode
Use the `-c` or `-console` argument to run the application in console mode.

It is recommended to use the portable version for console mode because the installer requires libraries necessary for the UI.

### List of Arguments:

| Argument             | Short Version | Parameters     | Description                                                                                             |
|----------------------|---------------|----------------|---------------------------------------------------------------------------------------------------------|
| `-console`           | `-c`          | -              | Run the application in console mode                                                                     |
| `-path`              | `-p`          | `path`         | Path to the directory to scan                                                                           |
| `-file`              | `-f`          | `file_path`    | Path to a file containing a list of paths to scan (one per line) (`.csv`, `.txt`)                       |
| `-extensions`        | `-e`          | `extensions`   | List of file extensions to scan, separated by commas                                                    |
| `-detect_functions`  | `-df`         | `functions`    | List of detection functions to use, separated by commas                                                 |
| `-user_signatures`   | `-us`         | `signatures`   | Custom signatures to search for, separated by commas                                                    |
| `-fast`              | -             | -              | Enable fast scan mode (mutually exclusive with `-full`)                                                 |
| `-full`              | -             | -              | Enable full scan (mutually exclusive with `-fast`)                                                      |
| `-report`            | `-r`          | `path`         | Path to save the report (default: application directory)                                                |
| `-report_encoding`   | `-re`         | `encoding`     | Report encoding (default: windows-1251 on Windows, UTF-8 on other OS)                                   |
| `-threads`           | `-t`          | `number`       | Number of threads for scanning (default: number of available processors)                                |
| `-help`              | `-h`          | -              | Help                                                                                                    |

### Usage Examples:

1. Simple directory scan:
   ```
   app -c -p /path/to/directory
   ```

2. Scan with specified extensions and save report:
   ```
   app -c -p /path/to/directory -e XLSX,PDF,Text -r /path/for/report
   ```

3. Using a file with a list of paths and user settings:
   ```
   app -c -f /путь/к/файлу_с_путями.txt -df CardNumbers,AccountNumber -us signature1 -t 4
   ```

4. Fast scan with specified report encoding:
   ```
   app -c -p /path/to/directory -fast -re UTF-8
   ```
