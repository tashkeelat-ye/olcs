import {
  Module,
} from '@nestjs/common';

import {
  PrismaService,
} from './prisma.service';

import {
  AuthModule,
} from './auth/auth.module';

import {
  DevicesModule,
} from './devices/devices.module';

import {
  LocationsModule,
} from './locations/locations.module';

import {
  SmsModule,
} from './sms/sms.module';

@Module({
  imports: [
    AuthModule,
    DevicesModule,
    LocationsModule,
    SmsModule,
  ],

  providers: [
    PrismaService,
  ],
})
export class AppModule {}
