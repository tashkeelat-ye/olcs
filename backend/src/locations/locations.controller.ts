import {
  Body,
  Controller,
  Get,
  Headers,
  Param,
  Post,
} from '@nestjs/common';

import {
  LocationsService,
} from './locations.service';

@Controller('v1/locations')
export class LocationsController {

  constructor(
    private readonly locations:
      LocationsService,
  ) {}

  @Post('batch')
  receiveBatch(
    @Headers('x-device-api-key')
    apiKey: string,

    @Body()
    body: {
      locations: any[];
    },
  ) {

    return this.locations.receiveBatch(
      apiKey,
      body.locations,
    );
  }

  @Get()
  getAll() {

    return this.locations.findAll();
  }

  @Get('device/:deviceId')
  getDeviceLocations(
    @Param('deviceId')
    deviceId: string,
  ) {

    return this.locations
      .findDeviceLocations(
        deviceId
      );
  }
}
