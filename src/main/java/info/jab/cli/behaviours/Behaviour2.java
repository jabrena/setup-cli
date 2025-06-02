package info.jab.cli.behaviours;

import io.vavr.control.Either;

interface Behaviour2 {
    Either<String, String> execute(String parameter1, String parameter2);
}
