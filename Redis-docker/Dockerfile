# Dockerfile for Redis Sentinel
FROM redis:7.4

# Copy the Sentinel configuration file into the image
COPY sentinel.conf /etc/redis/sentinel.conf

# Use the Redis Sentinel as the default command
CMD ["redis-sentinel", "/etc/redis/sentinel.conf"]
