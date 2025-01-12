FROM python:alpine

WORKDIR /app

COPY . /app/

RUN pip install --no-cache-dir -r requirements.txt
RUN pip install gunicorn

EXPOSE 5000

CMD ["gunicorn", "-w 4", "-b 0.0.0.0:5000", "main:app"]
