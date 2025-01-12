# Base image
FROM python:3.9-slim

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
ENV FLASK_APP=main.py
ENV FLASK_RUN_HOST=0.0.0.0

# Set the working directory
WORKDIR /app

# Copy the requirements file
COPY . .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the Flask app source code

# Expose the Flask app's port
EXPOSE 5000

# Run the Flask app
CMD ["flask", "run"]
