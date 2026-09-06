import {
  Device,
  Location,
} from './types';

const API_URL =
  import.meta.env
    .VITE_API_URL ||
  'http://localhost:3000';

export async function
getDevices():
  Promise<Device[]> {

  const response =
    await fetch(
      `${API_URL}/v1/devices`
    );

  if (!response.ok) {
    throw new Error(
      'Failed to load devices'
    );
  }

  return response.json();
}

export async function
getLocations():
  Promise<Location[]> {

  const response =
    await fetch(
      `${API_URL}/v1/locations`
    );

  if (!response.ok) {
    throw new Error(
      'Failed to load locations'
    );
  }

  return response.json();
}
