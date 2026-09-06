import {
  Injectable,
  UnauthorizedException,
  InternalServerErrorException,
} from '@nestjs/common';

import {
  PrismaService,
} from '../prisma.service';

import {
  compare,
} from 'bcrypt';

import {
  sign,
} from 'jsonwebtoken';

@Injectable()
export class AuthService {

  constructor(
    private readonly prisma:
      PrismaService,
  ) {}

  async login(
    email: string,
    password: string,
  ) {

    const jwtSecret =
      process.env.JWT_SECRET;

    if (!jwtSecret) {
      throw new InternalServerErrorException(
        'JWT_SECRET is not configured',
      );
    }

    const user =
      await this.prisma.user.findUnique({
        where: {
          email,
        },
      });

    if (!user) {
      throw new UnauthorizedException(
        'Invalid email or password',
      );
    }

    const valid =
      await compare(
        password,
        user.passwordHash,
      );

    if (!valid) {
      throw new UnauthorizedException(
        'Invalid email or password',
      );
    }

    const accessToken =
      sign(
        {
          sub: user.id,
          email: user.email,
        },
        jwtSecret,
        {
          expiresIn: '7d',
        },
      );

    return {
      accessToken,

      user: {
        id: user.id,
        email: user.email,
        name: user.name,
      },
    };
  }
}
