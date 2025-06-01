package info.jab.cli.behaviours;

import io.vavr.control.Either;

interface Behaviour1 {
    Either<String, String> execute(String parameter);
}
