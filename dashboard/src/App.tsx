import {
  useEffect,
  useState,
} from 'react';

import {
  getDevices,
  getLocations,
} from './api';

import {
  Device,
  Location,
} from './types';

function App() {

  const [
    devices,
    setDevices,
  ] =
    useState<Device[]>([]);

  const [
    locations,
    setLocations,
  ] =
    useState<Location[]>([]);

  const [
    loading,
    setLoading,
  ] =
    useState(true);

  const [
    error,
    setError,
  ] =
    useState('');

  async function load() {

    try {

      setError('');

      const [
        deviceData,
        locationData,
      ] =
        await Promise.all([
          getDevices(),
          getLocations(),
        ]);

      setDevices(
        deviceData
      );

      setLocations(
        locationData
      );

    } catch (
      error
    ) {

      console.error(
        error
      );

      setError(
        'تعذر الاتصال بالخادم'
      );

    } finally {

      setLoading(false);
    }
  }

  useEffect(() => {

    load();

    const timer =
      window.setInterval(
        load,
        10000
      );

    return () =>
      window.clearInterval(
        timer
      );

  }, []);

  return (

    <main>

      <header className="header">

        <div>

          <h1>
            OLCS
          </h1>

          <p>
            نظام إدارة وتتبع المواقع
          </p>

        </div>

        <button
          onClick={load}
        >
          تحديث
        </button>

      </header>

      {error && (

        <div className="error">
          {error}
        </div>

      )}

      {loading ? (

        <div className="loading">
          جاري تحميل البيانات...
        </div>

      ) : (

        <>

          <section className="stats">

            <div className="card">

              <span>
                الأجهزة
              </span>

              <strong>
                {devices.length}
              </strong>

            </div>

            <div className="card">

              <span>
                المواقع المسجلة
              </span>

              <strong>
                {locations.length}
              </strong>

            </div>

            <div className="card">

              <span>
                الأجهزة النشطة
              </span>

              <strong>
                {
                  devices.filter(
                    device =>
                      device.active
                  ).length
                }
              </strong>

            </div>

          </section>

          <section>

            <h2>
              الأجهزة
            </h2>

            <div className="devices">

              {devices.length === 0 ? (

                <div className="empty">

                  لا توجد أجهزة مسجلة.

                </div>

              ) : (

                devices.map(
                  device => (

                    <article
                      className="device"
                      key={device.id}
                    >

                      <div className="device-title">

                        <h3>
                          {device.name}
                        </h3>

                        <span
                          className={
                            device.active
                              ? 'online'
                              : 'offline'
                          }
                        >
                          {
                            device.active
                              ? 'نشط'
                              : 'متوقف'
                          }
                        </span>

                      </div>

                      <p>
                        Device ID:
                        {' '}
                        {device.id}
                      </p>

                      <p>
                        الموقع الأخير:
                        {' '}
                        {device.lastLatitude ??
                          'غير متوفر'}
                        {' , '}
                        {device.lastLongitude ??
                          'غير متوفر'}
                      </p>

                      <p>
                        الدقة:
                        {' '}
                        {device.lastAccuracy ??
                          '-'}
                        {' '}
                        متر
                      </p>

                      <p>
                        آخر اتصال:
                        {' '}
                        {device.lastSeenAt ??
                          'لا يوجد'}
                      </p>

                    </article>

                  )
                )

              )}

            </div>

          </section>

          <section>

            <h2>
              آخر المواقع
            </h2>

            <div className="locations">

              {locations
                .slice(0, 50)
                .map(
                  location => (

                    <article
                      className="location"
                      key={
                        `${location.deviceId}-${location.id}`
                      }
                    >

                      <strong>

                        {location.latitude}

                        {' , '}

                        {location.longitude}

                      </strong>

                      <span>
                        الجهاز:
                        {' '}
                        {location.deviceId}
                      </span>

                      <span>
                        الدقة:
                        {' '}
                        {location.accuracy ??
                          '-'}
                        m
                      </span>

                      <span>
                        النقل:
                        {' '}
                        {location.transport ??
                          'unknown'}
                      </span>

                      <span>
                        الوقت:
                        {' '}
                        {location.capturedAt}
                      </span>

                    </article>

                  )
                )}

            </div>

          </section>

        </>

      )}

    </main>
  );
}

export default App;
