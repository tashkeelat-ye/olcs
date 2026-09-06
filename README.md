# OLCS

Offline Location Communication System.

نظام تتبع مواقع للأجهزة التي تعمل بنظام Android.

## المكونات

- Android Agent
- GPS/GNSS
- Offline Local Queue
- Internet Sync
- SMS Fallback
- Backend API
- PostgreSQL
- Web Dashboard

## تدفق البيانات

GPS
↓
Local Database
↓
Internet
↓
Backend
↓
PostgreSQL

إذا لم يوجد Internet:

GPS
↓
Local Database
↓
SMS
↓
SMS Gateway
↓
Backend
↓
PostgreSQL

إذا لم يوجد Internet ولا SMS:

GPS
↓
Local Database
↓
انتظار
↓
إعادة المحاولة

## تشغيل PostgreSQL

docker compose up -d

## Backend

cd backend

npm install

npx prisma generate

npx prisma migrate dev --name init

npm run start:dev

## Dashboard

cd dashboard

npm install

npm run dev

## Android

افتح مجلد android في Android Studio.

قم بعمل Gradle Sync ثم Build APK.

## API

POST /v1/auth/login

GET /v1/devices

POST /v1/devices

POST /v1/locations/batch

GET /v1/locations

GET /v1/locations/device/:deviceId

POST /v1/sms/receive
