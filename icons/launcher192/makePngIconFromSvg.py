import sys
import glob
import os
import math

# ###############################################
def main():
    xxxhdpiSize = int(sys.argv[1])
    xxhdpiSize = math.ceil(xxxhdpiSize * 0.75)
    xhdpiSize = math.ceil(xxxhdpiSize * 0.5)
    hdpiSize = math.ceil(xxxhdpiSize * 0.375)
    mdpiSize = math.ceil(xxxhdpiSize * 0.25)
    os.system("mkdir xhdpi xxhdpi xxxhdpi mdpi hdpi")

    files = glob.glob("*.svg")
    for file in files:
        newFileName = file.replace(".svg", ".png")
        os.system("inkscape -z -e xxxhdpi/" + newFileName + " -w " + str(xxxhdpiSize) + " -h " + str(xxxhdpiSize) + " " + file)
        os.system("inkscape -z -e xxhdpi/" + newFileName + " -w " + str(xxhdpiSize) + " -h " + str(xxhdpiSize) + " " + file)
        os.system("inkscape -z -e xhdpi/" + newFileName + " -w " + str(xhdpiSize) + " -h " + str(xhdpiSize) + " " + file)
        os.system("inkscape -z -e hdpi/" + newFileName + " -w " + str(hdpiSize) + " -h " + str(hdpiSize) + " " + file)
        os.system("inkscape -z -e mdpi/" + newFileName + " -w " + str(mdpiSize) + " -h " + str(mdpiSize) + " " + file)


# ###############################################
if __name__ == "__main__":
    main()
