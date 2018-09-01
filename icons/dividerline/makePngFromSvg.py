import sys
import glob
import os
import math

# ###############################################
def main():
    xxxhdpiWidth = int(sys.argv[1])
    xxhdpiWidth = math.ceil(xxxhdpiWidth * 0.75)
    xhdpiWidth = math.ceil(xxxhdpiWidth * 0.5)
    hdpiWidth = math.ceil(xxxhdpiWidth * 0.375)
    mdpiWidth = math.ceil(xxxhdpiWidth * 0.25)

    xxxhdpiHeight = int(sys.argv[2])
    xxhdpiHeight = math.ceil(xxxhdpiHeight * 0.75)
    xhdpiHeight = math.ceil(xxxhdpiHeight * 0.5)
    hdpiHeight = math.ceil(xxxhdpiHeight * 0.375)
    mdpiHeight = math.ceil(xxxhdpiHeight * 0.25)

    os.system("mkdir xhdpi xxhdpi xxxhdpi mdpi hdpi")

    files = glob.glob("*.svg")
    for file in files:
        newFileName = file.replace(".svg", ".png")
        os.system("inkscape -z -e xxxhdpi/" + newFileName + " -w " + str(xxxhdpiWidth) + " -h " + str(xxxhdpiHeight) + " " + file)
        os.system("inkscape -z -e xxhdpi/" + newFileName + " -w " + str(xxhdpiWidth) + " -h " + str(xxhdpiHeight) + " " + file)
        os.system("inkscape -z -e xhdpi/" + newFileName + " -w " + str(xhdpiWidth) + " -h " + str(xhdpiHeight) + " " + file)
        os.system("inkscape -z -e hdpi/" + newFileName + " -w " + str(hdpiWidth) + " -h " + str(hdpiHeight) + " " + file)
        os.system("inkscape -z -e mdpi/" + newFileName + " -w " + str(mdpiWidth) + " -h " + str(mdpiHeight) + " " + file)


# ###############################################
if __name__ == "__main__":
    main()
