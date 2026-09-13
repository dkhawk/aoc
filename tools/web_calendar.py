#!/usr/bin/env python3
"""
web_calendar.py
Launches the Web-Based Master Calendar Application for Advent of Code.
Serves web/index.html locally and opens it in your default browser.
"""

import os
import sys
import subprocess
import http.server
import socketserver
import webbrowser

PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
WEB_DIR = os.path.join(PROJECT_ROOT, "web")
PORT = 8085

def update_data():
    print("Updating web calendar dataset...")
    gen_script = os.path.join(PROJECT_ROOT, "tools", "generate_web_data.py")
    subprocess.run([sys.executable, gen_script], check=True)

def serve():
    os.chdir(WEB_DIR)
    Handler = http.server.SimpleHTTPRequestHandler
    
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        url = f"http://localhost:{PORT}"
        print(f"=========================================================================")
        print(f" 🎅 ADVENT OF CODE WEB CALENDAR RUNNING AT: {url}")
        print(f"=========================================================================")
        print("Press Ctrl+C to stop the web server.\n")
        
        # Open in default web browser
        webbrowser.open(url)
        
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nShutting down Web Calendar server. Goodbye!")

def main():
    update_data()
    serve()

if __name__ == "__main__":
    main()
