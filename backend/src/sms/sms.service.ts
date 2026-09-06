import {
  Injectable,
} from '@nestjs/common';

import {
  createHmac,
  timingSafeEqual,
} from 'crypto';

import {
  PrismaService,
} from '../prisma.service';

@Injectable()
export class SmsService {

  constructor(
    private readonly prisma:
      PrismaService,
  ) {}

  parse(
    message: string,
  ) {

    const parts =
      message.split('|');

    if (parts.shift() !== 'OL1') {
      throw new Error(
        'Invalid OLCS SMS'
      );
    }

    const result:
      Record<string, string> = {};

    for (
      const part of parts
    ) {

      const index =
        part.indexOf('=');

      if (index < 0) continue;

      result[
        part.substring(0, index)
      ] =
        part.substring(index + 1);
    }

    return result;
  }

  async receive(
    message: string,
  ) {

    const data =
      this.parse(message);

    if (
      !data.D ||
      !data.E ||
      !data.L ||
      !data.T ||
      !data.S
    ) {

      throw new Error(
        'Incomplete SMS'
      );
    }

    const device =
      await this.prisma.device.findUnique({
        where: {
          id: data.D,
        },
      });

    if (!device || !device.active) {
      throw new Error(
        'Unknown device'
      );
    }

    const unsigned =
      message
        .split('|')
        .filter(
          part =>
            !part.startsWith('S=')
        )
        .join('|');

    const expected =
      createHmac(
        'sha256',
        device.smsSecret
      )
      .update(unsigned)
      .digest('hex');

    const received =
      Buffer.from(
        data.S,
        'utf8'
      );

    const calculated =
      Buffer.from(
        expected,
        'utf8'
      );

    if (
      received.length !==
      calculated.length ||
      !timingSafeEqual(
        received,
        calculated
      )
    ) {

      throw new Error(
        'Invalid signature'
      );
    }

    const [
      latitude,
      longitude,
    ] =
      data.L.split(',').map(
        Number
      );

    const capturedAt =
      new Date(
        Number(data.T)
      );

    const exists =
      await this.prisma.location.findUnique({
        where: {
          deviceId_id: {
            deviceId: device.id,
            id: data.E,
          },
        },
      });

    if (exists) {

      return {
        success: true,
        duplicate: true,
      };
    }

    await this.prisma.location.create({
      data: {

        id:
          data.E,

        deviceId:
          device.id,

        latitude,
        longitude,

        accuracy:
          data.A
            ? Number(data.A)
            : null,

        capturedAt,

        transport:
          'sms',
      },
    });

    await this.prisma.device.update({
      where: {
        id: device.id,
      },

      data: {
        lastLatitude: latitude,
        lastLongitude: longitude,
        lastAccuracy:
          data.A
            ? Number(data.A)
            : null,
        lastSeenAt:
          new Date(),
      },
    });

    return {
      success: true,
      duplicate: false,
    };
  }
}
