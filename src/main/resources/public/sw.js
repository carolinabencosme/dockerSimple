/*const CACHE_NAME = 'wornux-cache-v1';

const STATIC_ASSETS = [
    '/shortened',
    '/shortened/dashboard',
    '/css/quaters.css',
    '/css/tailwind.css',
    '/js/toggleSideMenu.js',
    '/js/toggleAvatarMenu.js',
    '/js/preview.js',
    '/sw.js',

];



self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            console.log('Caching static assets');
            return cache.addAll(STATIC_ASSETS);
        })
    );
});


// servir desde red si se puede, si no, desde cache
self.addEventListener('fetch', (event) => {
    event.respondWith(
        fetch(event.request)
            .then((response) => {
                return response;
            })
            .catch(() => {
                return caches.match(event.request);
            })
    );
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames
                    .filter(name => name !== CACHE_NAME)
                    .map(name => caches.delete(name))
            );
        })
    );
});
*/

const CACHE_NAME = `wornux-cache-${Date.now()}`;
const STATIC_ASSETS = [
    '/shortened',
    '/shortened/dashboard',
    '/css/quaters.css',
    '/css/tailwind.css',
    '/js/toggleSideMenu.js',
    '/js/toggleAvatarMenu.js',
    '/js/preview.js',
    '/sw.js',
];

self.addEventListener('install', event => {
    self.skipWaiting();
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(STATIC_ASSETS))
    );
});

self.addEventListener('activate', event => {
    event.waitUntil(
        Promise.all([
            self.clients.claim(),
            caches.keys().then(keys =>
                Promise.all(
                    keys.filter(key => key !== CACHE_NAME)
                        .map(key => caches.delete(key))
                )
            )
        ])
    );
});

self.addEventListener('fetch', event => {
    if (event.request.method !== 'GET') return;

    event.respondWith(
        fetch(event.request)
            .then(networkRes => {
                const resClone = networkRes.clone();
                caches.open(CACHE_NAME).then(cache => cache.put(event.request, resClone));
                return networkRes;
            })
            .catch(() =>
                caches.match(event.request)
            )
    );
});


