# Maintenance

Some **User prompts** designed to help in the maintenance of this repository.

```bash
# Prompt to provide a release changelog
Can you update the current changelog for 0.12.0 comparing git commits in relation to 0.9.0 tag. Use  @https://keepachangelog.com/en/1.1.0/  rules

#Bump to a new snapshot
@resources/ update version to 0.13.0-SNAPSHOT and pom.xml and maven modules
```

## Release process

- [ ] Run regression tests
- [ ] Update changelog
- [ ] Remove SNAPSHOT from .md & pom.xml
- [ ] Last review in docs (Manual)
- [ ] Review git changes for hidden issues (Manual) https://github.com/jabrena/setup-cli/compare/0.11.0...feature/release
- [ ] Tag repository
- [ ] Create release
- [ ] Update https://github.com/jabrena/jbang-catalog


---

```bash
# Prompt to provide a release changelog
Can you update the current changelog for 0.12.0 comparing git commits in relation to 0.11.0 tag. Use  @https://keepachangelog.com/en/1.1.0/  rules

## Tagging process
git tag --list
git tag 0.11.0
git push --tags
```
