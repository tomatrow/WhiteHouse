#!/bin/bash

if [ "$(uname)" == "Darwin" ]; then
    PARAM="$PARAM -XstartOnFirstThread"
    if [ "$(uname -m)" = "x86_64" ]; then
        SWT="lib/swt-mac64.jar"
    else
        SWT="lib/swt-mac.jar"
    fi
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    if [ "$(uname -m)" = "x86_64" ]; then
        SWT="lib/swt-linux64.jar"
    else
        SWT="lib/swt-linux.jar"
    fi
fi

echo "Using library $SWT ..."
if [ -n "$PARAM" ]
    echo "Using parameters $PARAM ..."
fi

if [ "$1" == "compile" ]
    javac $PARAM -cp .:$SWT -d . *.java
elif [ "$1" == "run" ]
    java $PARAM -cp .:$SWT com.github.redhatter.whitehouse.WhiteHouse
fi
