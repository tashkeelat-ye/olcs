import {
  Body,
  Controller,
  Headers,
  UnauthorizedException,
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
    @Headers('x-sms-gateway-secret')
    gatewaySecret: string,

    @Body()
    body: {
      message: string;
    },
  ) {

    const expectedSecret =
      process.env.SMS_GATEWAY_SECRET;

    if (!expectedSecret) {
      throw new UnauthorizedException(
        'SMS gateway is not configured',
      );
    }

    if (
      !gatewaySecret ||
      gatewaySecret !== expectedSecret
    ) {
      throw new UnauthorizedException(
        'Invalid SMS gateway credentials',
      );
    }

    if (
      !body ||
      typeof body.message !== 'string' ||
      body.message.length === 0
    ) {
      throw new UnauthorizedException(
        'SMS message is required',
      );
    }

    return this.sms.receive(
      body.message,
    );
  }
}
