const readlineSync = require('readline-sync');
const fs = require('fs');
const path = require('path');
const { ContextExclusionPlugin } = require('webpack');

const changelogDir = path.join(process.cwd(), 'src/main/resources/config/liquibase/changelog');

const fileEntries = fs.readdirSync(changelogDir, { withFileTypes: true });

const changelogMap = fileEntries.reduce((map, fileEntry) => {
  if (!fileEntry.isFile()) {
    return map;
  }
  const match = /^([0-9]+)_(.+).xml$/.exec(fileEntry.name);
  if (match === null) {
    return map;
  }
  const stem = match[2];
  let changelog = map.get(stem);
  if (changelog === undefined) {
    changelog = {
      stem,
      files: []
    };
    map.set(stem, changelog);
  }
  changelog.files.push({
    ts: match[1],
    name: fileEntry.name,
    full: path.join(changelogDir, fileEntry.name),
  });
  return map;
}, new Map());

// Filter out changelogs with only a single file. There is nothing to merge.
const changelogs = Array.from(changelogMap.values())
  .filter(cl => cl.files.length > 1);

// Sort from old to new.
for (const changelog of changelogs) {
  changelog.files.sort((a, b) => a.ts > b.ts ? a : b);
}

// Make sure that there are only two files per changelog.
const changelogsWithTooManyFiles = changelogs.filter(cl => cl.files.length > 2);
if (changelogsWithTooManyFiles.length > 0) {
  console.log('There exist changelogs with more than two files. Only two files can be merged at a time. These changelogs are affected:')
  for (const changelog of changelogsWithTooManyFiles) {
    console.log();
    console.log(`${changelog.stem}`);
    for (const file of changelog.files) {
      console.log(`    - ${file.name}`);
    }
    console.log();
  }
  process.exit(-1);
  return;
}

if (changelogs.length === 0) {
  console.log('There are no changes.');
  process.exit(0);
  return;
}

for (const changelog of changelogs) {
  changelog.oldFile = changelog.files[0];
  changelog.newFile = changelog.files[1];

  // Read content from new file. Replace all occurrences of the ID (timestamp)
  // with the old timestamp.
  const newContent = fs.readFileSync(changelog.newFile.full, { encoding: 'utf8' })
    .replace(new RegExp(changelog.newFile.ts, 'g'), changelog.oldFile.ts);

  const oldContent = fs.readFileSync(changelog.oldFile.full, { encoding: 'utf8' });
  changelog.content = newContent;
  changelog.changed = oldContent !== newContent;
}

console.log('The following changes will be made to the changelogs:');
console.log();
console.log(`Changelog directory: ${changelogDir}`);
console.log();

for (const changelog of changelogs) {
  console.log(changelog.stem);
  if (changelog.changed) {
    console.log(`    Delete  ${changelog.oldFile.name}`);
    console.log(`    Rename  ${changelog.newFile.name}  ->  ${changelog.oldFile.name}`);
  } else {
    console.log(`    Delete  ${changelog.newFile.name} (unchanged)`);
  }
  console.log();
}

if (!readlineSync.keyInYN('Do you want continue?')) {
  console.log('Aborting...');
  process.exit(0);
  return;
}

console.log();
console.log('Renaming files:');

// For padding.
const maxNewFileLength = changelogs.reduce((max, cl) => Math.max(cl.newFile.name.length, max), 0);

for (const changelog of changelogs) {
  if (changelog.changed) {
    fs.renameSync(changelog.newFile.full, changelog.oldFile.full);
    fs.writeFileSync(changelog.oldFile.full, changelog.content, { encoding: 'utf8' });
    console.log(`    ${changelog.newFile.name.padEnd(maxNewFileLength, ' ')}  ->  ${changelog.oldFile.name}`);
  } else {
    fs.unlinkSync(changelog.newFile.full);
    console.log(`    ${changelog.newFile.name.padEnd(maxNewFileLength, ' ')}  ->  deleted`);
  }
}

console.log();
