# Nro Version

Dự án gồm game backend Java, Swing management UI tùy chọn và website PHP riêng.

## Môi trường

- JDK 17 hoặc JDK 21
- Maven 3.9+
- MariaDB/MySQL
- Swing UI cần máy có desktop/X11; backend không cần GUI

Ubuntu 24:

```bash
sudo apt update
sudo apt install openjdk-17-jdk maven mariadb-client
java -version
mvn -version
```

## Database

Backend đọc cấu hình từ `data/config/data_base.properties` và mặc định dùng database `nro`.

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS nro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p nro < database/nro.sql
```

## Build

```bash
mvn clean verify
```

Artifact được tạo:

```text
backend/target/game-backend.jar
management-ui/target/management-ui.jar
```

`data/` phải nằm cùng project root khi chạy vì không được đóng gói vào JAR.

## Run backend

Linux:

```bash
chmod +x scripts/run-backend.sh
./scripts/run-backend.sh
```

Windows:

```bat
scripts\run-backend.bat
```

Hoặc chạy trực tiếp:

```bash
java -jar backend/target/game-backend.jar
```

## Run management UI

Chỉ chạy trên máy có giao diện đồ họa:

```bash
java -jar management-ui/target/management-ui.jar
```

## React management web

The web console is in `management-web/`. It uses the Java Admin API on
`127.0.0.1:18080`; the API never exposes database credentials to the browser.

```bash
cd management-web
npm ci
npm run dev
```

For production, run `npm run build`, serve `management-web/dist` through Nginx
over HTTPS, and proxy `/api/` to the loopback Admin API. The sample Nginx
configuration is `deploy/nginx/nro-management.conf`. Set
`admin.cookie.secure=true` and `admin.api.cors=https://<your-admin-host>` in
`data/config/data_base.properties`.

The web console keeps compatibility with the legacy `account.password` value,
but never returns it to the browser. Migrating that field to a modern password
hash is a follow-up security task.

## Ubuntu systemd

Copy `deploy/systemd/nro.service` vào `/etc/systemd/system/`, chỉnh `User` và đường dẫn nếu cần, sau đó:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now nro
sudo journalctl -u nro -f
```

## Website PHP với Apache

`website/` là thành phần PHP độc lập, không thuộc Maven reactor. Website sử dụng Apache để xử lý `.htaccess`, URL rewrite và PHP.

### Cấu hình chung

Tạo file cấu hình môi trường và cài package:

```bash
cd website
cp .env.example .env
composer install
```

Sửa `website/.env` theo database website:

```env
DB_HOST=127.0.0.1
DB_NAME=nro
DB_USER=root
DB_PASS=
DB_HOST2=127.0.0.1
DB_NAME2=nro
DB_USER2=root
DB_PASS2=
GAME_ADMIN_HOST=127.0.0.1
GAME_ADMIN_PORT=4424
```

Import database website:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS nro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p nro < ../database/nro.sql
```

`vendor/` không lưu trong repository; Composer sẽ tạo lại thư mục này từ `composer.lock`.

### Windows / XAMPP

1. Cài XAMPP có Apache, PHP và MariaDB/MySQL; cài thêm Composer.
2. Mở XAMPP Control Panel và start Apache, MySQL.
3. Tạo database `nro` bằng phpMyAdmin hoặc lệnh `mysql`, sau đó import `database/nro.sql`.
4. Trong PowerShell:

```powershell
cd D:\AWN_Version\AWN_Version\website
Copy-Item .env.example .env
composer install
```

5. Thêm VirtualHost vào `C:\xampp\apache\conf\extra\httpd-vhosts.conf`:

```apache
<VirtualHost *:80>
    ServerName awn.local
    DocumentRoot "D:/AWN_Version/AWN_Version/website"

    <Directory "D:/AWN_Version/AWN_Version/website">
        AllowOverride All
        Require all granted
        DirectoryIndex Trang-Chu.php
    </Directory>
</VirtualHost>
```

6. Thêm vào `C:\Windows\System32\drivers\etc\hosts`:

```text
127.0.0.1 awn.local
```

Restart Apache rồi mở:

```text
http://awn.local/
```

Nếu không dùng XAMPP mặc định, thay lại đường dẫn `DocumentRoot` cho đúng vị trí project.

### Ubuntu 24 / Apache2

Cài Apache, PHP, extension cần thiết, Composer và MariaDB:

```bash
sudo apt update
sudo apt install apache2 mariadb-server php php-cli libapache2-mod-php \
  php-mysql php-curl php-mbstring php-xml php-zip php-gd composer unzip
```

Ví dụ đặt project tại `/var/www/awn-game`, sau đó chạy:

```bash
cd /var/www/awn-game
cp website/.env.example website/.env
composer install --working-dir=website --no-dev

sudo mariadb -e "CREATE DATABASE IF NOT EXISTS nro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
sudo mariadb nro < database/nro.sql
```

Bật các module Apache cần cho `.htaccess`:

```bash
sudo a2enmod rewrite headers deflate access_compat
```

`access_compat` cần thiết vì `.htaccess` hiện có các directive tương thích Apache cũ như `Order allow,deny`.

Tạo file `/etc/apache2/sites-available/awn-website.conf`:

```apache
<VirtualHost *:80>
    ServerName awn.local
    DocumentRoot /var/www/awn-game/website
    DirectoryIndex Trang-Chu.php

    <Directory /var/www/awn-game/website>
        Options FollowSymLinks
        AllowOverride All
        Require all granted
    </Directory>

    ErrorLog ${APACHE_LOG_DIR}/awn-website-error.log
    CustomLog ${APACHE_LOG_DIR}/awn-website-access.log combined
</VirtualHost>
```

Kích hoạt website:

```bash
sudo a2ensite awn-website.conf
sudo apache2ctl configtest
sudo systemctl reload apache2
```

Thêm domain local nếu chạy thử trên máy chủ:

```bash
echo "127.0.0.1 awn.local" | sudo tee -a /etc/hosts
```

Mở website tại:

```text
http://awn.local/
```

Website có thể chạy độc lập với Java backend. Các chức năng cần gọi Java server phải cấu hình `GAME_ADMIN_HOST` và `GAME_ADMIN_PORT` trong `.env` đúng với API quản trị đang triển khai.

Script `website/auto-load-api.bat` chỉ phục vụ tác vụ polling API tùy chọn; không cần chạy script này để phục vụ website bằng Apache.
