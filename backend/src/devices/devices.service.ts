import {
  Injectable,
  NotFoundException,
} from '@nestjs/common';

import {
  randomBytes,
} from 'crypto';

import {
  PrismaService,
} from '../prisma.service';

@Injectable()
export class DevicesService {

  constructor(
    private readonly prisma:
      PrismaService,
  ) {}

  async create(
    name: string,
    phoneNumber?: string,
  ) {

    const id =
      `device_${randomBytes(8).toString('hex')}`;

    const apiKey =
      randomBytes(32).toString('hex');

    const smsSecret =
      randomBytes(32).toString('hex');

    return this.prisma.device.create({
      data: {
        id,
        name,
        phoneNumber,
        apiKey,
        smsSecret,
      },
    });
  }

  async findAll() {

    return this.prisma.device.findMany({
      orderBy: {
        createdAt: 'desc',
      },
    });
  }

  async findByApiKey(
    apiKey: string,
  ) {

    return this.prisma.device.findUnique({
      where: {
        apiKey,
      },
    });
  }

  async updateLocation(
    deviceId: string,
    latitude: number,
    longitude: number,
    accuracy?: number,
  ) {

    return this.prisma.device.update({
      where: {
        id: deviceId,
      },

      data: {
        lastLatitude: latitude,
        lastLongitude: longitude,
        lastAccuracy: accuracy,
        lastSeenAt: new Date(),
      },
    });
  }

  async get(
    id: string,
  ) {

    const device =
      await this.prisma.device.findUnique({
        where: {
          id,
        },
      });

    if (!device) {
      throw new NotFoundException(
        'Device not found'
      );
    }

    return device;
  }
}
