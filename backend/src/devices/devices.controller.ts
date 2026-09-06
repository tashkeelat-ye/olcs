import {
  Body,
  Controller,
  Get,
  Param,
  Post,
} from '@nestjs/common';

import {
  DevicesService,
} from './devices.service';

@Controller('v1/devices')
export class DevicesController {

  constructor(
    private readonly devices:
      DevicesService,
  ) {}

  @Get()
  getAll() {
    return this.devices.findAll();
  }

  @Get(':id')
  get(
    @Param('id')
    id: string,
  ) {
    return this.devices.get(id);
  }

  @Post()
  create(
    @Body()
    body: {
      name: string;
      phoneNumber?: string;
    },
  ) {

    return this.devices.create(
      body.name,
      body.phoneNumber,
    );
  }
}
