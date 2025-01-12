from dotenv import load_dotenv
from os import getenv

load_dotenv(".env")


class Config:
    SECRET_KEY = getenv("FLASK_SECRET")
    GROQ_API_KEY = getenv("GROQ_API_KEY")
