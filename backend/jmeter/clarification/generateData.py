filename = "ClarificationData.csv"

# Data specific variables
title = "TITLE:"
description = "DESCRIPTION:"
status = "OPEN" 


#Code

i = 0
with open(filename, 'w') as f:
    while i < 100:
        f.write(title + str(i) + "," + description + str(i) + "," + status + "\n")
        i+=1
    

