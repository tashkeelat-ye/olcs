import {
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';

import {
  DevicesService,
} from '../devices/devices.service';

import {
  PrismaService,
} from '../prisma.service';

@Injectable()
export class LocationsService {

  constructor(
    private readonly prisma:
      PrismaService,

    private readonly devices:
      DevicesService,
  ) {}

  async receiveBatch(
    apiKey: string,
    locations: any[],
  ) {

    const device =
      await this.devices.findByApiKey(
        apiKey
      );

    if (!device || !device.active) {
      throw new UnauthorizedException(
        'Invalid device'
      );
    }

    const accepted: string[] = [];
    const duplicates: string[] = [];
    const rejected: string[] = [];

    for (const item of locations) {

      if (
        typeof item.eventId !== 'string' ||
        typeof item.latitude !== 'number' ||
        typeof item.longitude !== 'number'
      ) {

        rejected.push(
          item.eventId || 'unknown'
        );

        continue;
      }

      const exists =
        await this.prisma.location.findUnique({
          where: {
            deviceId_id: {
              deviceId: device.id,
              id: item.eventId,
            },
          },
        });

      if (exists) {

        duplicates.push(
          item.eventId
        );

        continue;
      }

      const location =
        await this.prisma.location.create({
          data: {

            id:
              item.eventId,

            deviceId:
              device.id,

            latitude:
              item.latitude,

            longitude:
              item.longitude,

            accuracy:
              item.accuracy ?? null,

            altitude:
              item.altitude ?? null,

            speed:
              item.speed ?? null,

            bearing:
              item.bearing ?? null,

            capturedAt:
              new Date(
                item.capturedAt
              ),

            transport:
              'internet',
          },
        });

      await this.devices.updateLocation(
        device.id,
        location.latitude,
        location.longitude,
        location.accuracy ?? undefined,
      );

      accepted.push(
        location.id
      );
    }

    return {
      accepted,
      duplicates,
      rejected,
    };
  }

  async findAll() {

    return this.prisma.location.findMany({
      orderBy: {
        capturedAt: 'desc',
      },

      take: 1000,
    });
  }

  async findDeviceLocations(
    deviceId: string,
  ) {

    return this.prisma.location.findMany({

      where: {
        deviceId,
      },

      orderBy: {
        capturedAt: 'asc',
      },

      take: 5000,
    });
  }
}
