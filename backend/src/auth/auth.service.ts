import {
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';

import * as bcrypt from 'bcrypt';

import * as jwt from 'jsonwebtoken';

import {
  PrismaService,
} from '../prisma.service';

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

    const user =
      await this.prisma.user.findUnique({
        where: {
          email,
        },
      });

    if (!user) {
      throw new UnauthorizedException(
        'Invalid credentials'
      );
    }

    const valid =
      await bcrypt.compare(
        password,
        user.passwordHash,
      );

    if (!valid) {
      throw new UnauthorizedException(
        'Invalid credentials'
      );
    }

    const token =
      jwt.sign(
        {
          sub: user.id,
          email: user.email,
        },

        process.env.JWT_SECRET ||
          'development-secret',

        {
          expiresIn: '7d',
        }
      );

    return {
      accessToken: token,

      user: {
        id: user.id,
        email: user.email,
        name: user.name,
      },
    };
  }
}
