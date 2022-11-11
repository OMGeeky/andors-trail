@echo off
echo Creating folder for worlds
mkdir %~dp0\res\xml\worlds
echo Creating worlds
python %~dp0\tools\create_worlds.py %~dp0\res\xml\
echo Done!
# pause