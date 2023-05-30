# Base image
FROM node:16-alpine

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the Vue application
RUN npm run build

# Expose port 3001
EXPOSE 3001

# Run the application
CMD ["npm", "run", "dev"]