sudo docker build -t osrsbot-image .
sudo ps -a
sudo rm <id>
sudo docker run --network=host -e DISPLAY=unix$DISPLAY -t --rm osrsbot-image