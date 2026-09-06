export interface Device {

  id: string;

  name: string;

  phoneNumber?: string | null;

  apiKey?: string;

  active: boolean;

  lastLatitude?:
    number | null;

  lastLongitude?:
    number | null;

  lastAccuracy?:
    number | null;

  lastSeenAt?:
    string | null;
}

export interface Location {

  id: string;

  deviceId: string;

  latitude: number;

  longitude: number;

  accuracy?:
    number | null;

  altitude?:
    number | null;

  speed?:
    number | null;

  bearing?:
    number | null;

  capturedAt: string;

  receivedAt: string;

  transport?:
    string | null;
}
