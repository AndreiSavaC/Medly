from flask import Flask
from dotenv import load_dotenv

load_dotenv(".env")


def init_app():
    app = Flask(__name__, instance_relative_config=False)
    app.config.from_object("config.Config")

    with app.app_context():
        from routes.routes_groq import groq_bp

        app.register_blueprint(groq_bp)

        return app
