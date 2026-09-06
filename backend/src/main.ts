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
      AppModule
    );

  app.enableCors({
    origin:
      process.env.CORS_ORIGIN || '*',
  });

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
    })
  );

  const port =
    Number(
      process.env.API_PORT || 3000
    );

  await app.listen(port);

  console.log(
    `OLCS API running on port ${port}`
  );
}

bootstrap();
