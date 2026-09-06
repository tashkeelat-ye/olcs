import {
  Body,
  Controller,
  Post,
} from '@nestjs/common';

import {
  SmsService,
} from './sms.service';

@Controller('v1/sms')
export class SmsController {

  constructor(
    private readonly sms:
      SmsService,
  ) {}

  @Post('receive')
  receive(
    @Body()
    body: {
      message: string;
    },
  ) {

    return this.sms.receive(
      body.message
    );
  }
}
