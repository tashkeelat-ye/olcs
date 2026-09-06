import {
  Module,
} from '@nestjs/common';

import {
  SmsController,
} from './sms.controller';

import {
  SmsService,
} from './sms.service';

import {
  DevicesModule,
} from '../devices/devices.module';

import {
  PrismaService,
} from '../prisma.service';

@Module({
  imports: [
    DevicesModule,
  ],

  controllers: [
    SmsController,
  ],

  providers: [
    SmsService,
    PrismaService,
  ],
})
export class SmsModule {}
