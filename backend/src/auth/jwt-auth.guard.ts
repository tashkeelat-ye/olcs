import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';

import {
  Request,
} from 'express';

import {
  verify,
  JwtPayload,
} from 'jsonwebtoken';

export interface AuthenticatedRequest
  extends Request {
  user?: {
    id: string;
    email: string;
  };
}

@Injectable()
export class JwtAuthGuard
  implements CanActivate {

  canActivate(
    context: ExecutionContext,
  ): boolean {

    const request =
      context.switchToHttp()
        .getRequest<AuthenticatedRequest>();

    const authorization =
      request.headers.authorization;

    if (!authorization) {
      throw new UnauthorizedException(
        'Authorization header is required',
      );
    }

    const [
      scheme,
      token,
    ] =
      authorization.split(' ');

    if (
      scheme !== 'Bearer' ||
      !token
    ) {
      throw new UnauthorizedException(
        'Invalid authorization format',
      );
    }

    const secret =
      process.env.JWT_SECRET;

    if (!secret) {
      throw new UnauthorizedException(
        'JWT authentication is not configured',
      );
    }

    try {

      const payload =
        verify(
          token,
          secret,
        ) as JwtPayload;

      if (
        typeof payload.sub !== 'string' ||
        typeof payload.email !== 'string'
      ) {
        throw new UnauthorizedException(
          'Invalid token payload',
        );
      }

      request.user = {
        id: payload.sub,
        email: payload.email,
      };

      return true;

    } catch {
      throw new UnauthorizedException(
        'Invalid or expired access token',
      );
    }
  }
}
