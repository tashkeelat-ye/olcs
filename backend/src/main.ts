import {
  ValidationPipe,
} from '@nestjs/common';

import {
  NestFactory,
} from '@nestjs/core';

import {
  AppModule,
} from './app.module';

async function bootstrap() {

  const app =
    await NestFactory.create(
      AppModule,
    );

  const corsOrigin =
    process.env.CORS_ORIGIN;

  app.enableCors({
    origin:
      corsOrigin
        ? corsOrigin
        : false,

    methods: [
      'GET',
      'POST',
      'PUT',
      'PATCH',
      'DELETE',
      'OPTIONS',
    ],

    allowedHeaders: [
      'Content-Type',
      'Authorization',
      'X-Device-Api-Key',
      'X-SMS-Gateway-Secret',
    ],
  });

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      forbidNonWhitelisted: false,
    }),
  );

  const port =
    Number(
      process.env.API_PORT || 3000,
    );

  await app.listen(
    port,
    '0.0.0.0',
  );

  console.log(
    `OLCS API running on port ${port}`,
  );
}

bootstrap();
