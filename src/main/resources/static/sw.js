// Service Worker rahisi - inaruhusu simu "kuhifadhi" app hii kama app halisi.
// Haihitaji mantiki ngumu kwa mradi wetu - kazi yake kuu ni "kuwepo" ili
// browser ithibitishe app hii inastahili kuwa "installable".

const CACHE_NAME = 'simreg-cache-v1';
const urlsToCache = ['/', '/manifest.json'];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(urlsToCache))
  );
});

self.addEventListener('fetch', (event) => {
  // Kwa maombi ya HTML (ukurasa mzima), jaribu network kwanza; ukikosa internet, tumia cache
  event.respondWith(
    fetch(event.request).catch(() => caches.match(event.request))
  );
});
