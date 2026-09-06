import {
  Body,
  Controller,
  Get,
  Headers,
  Param,
  Post,
  UnauthorizedException,
  UseGuards,
} from '@nestjs/common';

import {
  LocationsService,
} from './locations.service';

import {
  JwtAuthGuard,
} from '../auth/jwt-auth.guard';

@Controller('v1/locations')
export class LocationsController {

  constructor(
    private readonly locations:
      LocationsService,
  ) {}

  /**
   * Android Location Agent
   *
   * Authentication:
   * X-Device-Api-Key
   */
  @Post('batch')
  receiveBatch(
    @Headers('x-device-api-key')
    apiKey: string,

    @Body()
    body: {
      locations: any[];
    },
  ) {

    if (!apiKey) {
      throw new UnauthorizedException(
        'Device API key is required',
      );
    }

    return this.locations.receiveBatch(
      apiKey,
      body.locations,
    );
  }

  /**
   * Dashboard
   *
   * Authentication:
   * JWT
   */
  @Get()
  @UseGuards(JwtAuthGuard)
  getAll() {
    return this.locations.findAll();
  }

  /**
   * Dashboard
   *
   * Authentication:
   * JWT
   */
  @Get('device/:deviceId')
  @UseGuards(JwtAuthGuard)
  getDeviceLocations(
    @Param('deviceId')
    deviceId: string,
  ) {
    return this.locations.findDeviceLocations(
      deviceId,
    );
  }
}
