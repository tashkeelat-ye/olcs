import {
  Module,
} from '@nestjs/common';

import {
  LocationsController,
} from './locations.controller';

import {
  LocationsService,
} from './locations.service';

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
    LocationsController,
  ],

  providers: [
    LocationsService,
    PrismaService,
  ],
})
export class LocationsModule {}
