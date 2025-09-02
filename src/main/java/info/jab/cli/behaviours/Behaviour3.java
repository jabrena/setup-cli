package info.jab.cli.behaviours;

import io.vavr.control.Either;

interface Behaviour3 {
    Either<String, String> execute(String parameter1, String parameter2, String parameter3);
}