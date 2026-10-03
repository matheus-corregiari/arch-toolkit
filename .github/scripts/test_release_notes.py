import importlib.util
from pathlib import Path
import unittest

spec = importlib.util.spec_from_file_location(
    "release_notes", Path(__file__).with_name("extract-release-notes.py")
)
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


class ReleaseNotesTests(unittest.TestCase):
    def test_exact_version_excludes_adjacent_releases(self):
        changelog = "# Changelog\n\n## 1.0.10\nNewer\n\n## 1.0.1\n### Changes\nTarget\n\n## 1.0.0\nOlder\n"
        self.assertEqual(module.extract(changelog, "1.0.1"), "## 1.0.1\n### Changes\nTarget\n")

    def test_last_section_and_crlf(self):
        self.assertEqual(module.extract("## 1.0.0\r\nNotes\r\n", "1.0.0"), "## 1.0.0\r\nNotes\n")

    def test_missing_version_uses_generated_notes(self):
        self.assertEqual(module.extract("## 1.0.0\nNotes\n", "1.0.1"), "")


if __name__ == "__main__":
    unittest.main()
